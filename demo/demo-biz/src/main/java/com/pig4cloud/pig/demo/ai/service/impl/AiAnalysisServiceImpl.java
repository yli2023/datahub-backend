package com.pig4cloud.pig.demo.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pig4cloud.pig.demo.ai.config.AiProperties;
import com.pig4cloud.pig.demo.ai.dto.*;
import com.pig4cloud.pig.demo.ai.service.*;
import com.pig4cloud.pig.demo.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

	private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final DemoService demoService;

	private final SimpleKeywordRetriever retriever;

	private final LlmClient llmClient;

	private final PromptTemplateService promptTemplateService;

	private final AiProperties aiProperties;

	private final AiAuditService aiAuditService;

	private final ObjectMapper objectMapper;

	@Override
	public AiAnalysisResponse analyze(AiAnalysisRequest request) {
		long t0 = System.currentTimeMillis();
		String traceId = aiAuditService.newTraceId();
		boolean usedLlm = false;
		boolean success = false;
		try {
			validate(request);
			LocalDateTime start = LocalDateTime.parse(request.getStartTime().trim(), TS);
			LocalDateTime end = LocalDateTime.parse(request.getEndTime().trim(), TS);
			int num = request.getNum() != null ? request.getNum() : 0;

			var times = demoService.getCreateTimeSlice(request.getTableName(), start, end);
			var s1 = demoService.getProcessedColumnSlice(request.getTableName(), start, end, request.getY1Name(), num);
			var s2 = demoService.getProcessedColumnSlice(request.getTableName(), start, end, request.getY2Name(), num);

			var facts = TimeSeriesFeatureExtractor.analyze(times, s1, s2, request.getY1Name(), request.getY2Name());
			List<String> ruleAnomalies = TimeSeriesFeatureExtractor.ruleAnomalyNotes(facts);
			String factsText = TimeSeriesFeatureExtractor.toFactsText(facts);
			factsText += midnightWarning(start);

			String retrievalQuery = String.join(" ", request.getTableName(), request.getY1Name(), request.getY2Name(),
					factsText, Optional.ofNullable(request.getFollowUpQuestion()).orElse(""));
			List<CitationDto> citations = retriever.retrieve(retrievalQuery);
			String kbText = formatCitationsForPrompt(citations);

			String followUpSection = buildFollowUpSection(request);

			List<Map<String, String>> messages = new ArrayList<>();
			messages.add(Map.of("role", "system", "content", promptTemplateService.systemPrompt()));
			trimHistory(request).forEach(m -> messages.add(Map.of("role", m.getRole(), "content", m.getContent())));
			String userContent = promptTemplateService.userAnalysisPrompt(factsText, kbText, followUpSection);
			messages.add(Map.of("role", "user", "content", userContent));

			AiAnalysisResponse parsed;
			if (aiProperties.isEnabled() && StringUtils.hasText(aiProperties.getApiKey())
					&& StringUtils.hasText(aiProperties.getBaseUrl())) {
				usedLlm = true;
				String raw = llmClient.chatCompletions(messages);
				parsed = parseLlmJson(raw);
			}
			else {
				parsed = ruleOnlyResponse(facts, request);
			}
			normalizeIfNoRealAnomaly(parsed, ruleAnomalies);
			parsed.setCitations(citations);
			parsed.setPlainText(buildPlainText(parsed));
			parsed.setMeta(AiResponseMeta.builder()
				.latencyMs(System.currentTimeMillis() - t0)
				.promptVersion(promptTemplateService.version())
				.model(usedLlm ? aiProperties.getModel() : "none")
				.usedLlm(usedLlm)
				.traceId(traceId)
				.build());
			success = true;
			return parsed;
		}
		catch (Exception e) {
			log.warn("AI analyze failed trace={}", traceId, e);
			AiAnalysisResponse err = AiAnalysisResponse.builder()
				.summary("分析失败: " + e.getMessage())
				.anomalyNotes(List.of())
				.possibleCauses(List.of())
				.actions(List.of("请检查时间范围、列名与表名；起始时间避免 00:00:00（滤波窗口特性）。"))
				.confidence(0.0)
				.citations(List.of())
				.plainText("")
				.meta(AiResponseMeta.builder()
					.latencyMs(System.currentTimeMillis() - t0)
					.promptVersion(promptTemplateService.version())
					.model("error")
					.usedLlm(usedLlm)
					.traceId(traceId)
					.build())
				.build();
			return err;
		}
		finally {
			aiAuditService.record(success, usedLlm, System.currentTimeMillis() - t0);
		}
	}

	private static void validate(AiAnalysisRequest r) {
		if (!StringUtils.hasText(r.getTableName())) {
			throw new IllegalArgumentException("tableName 不能为空");
		}
		if (!StringUtils.hasText(r.getY1Name()) || !StringUtils.hasText(r.getY2Name())) {
			throw new IllegalArgumentException("y1/y2 列名不能为空");
		}
		if (!StringUtils.hasText(r.getStartTime()) || !StringUtils.hasText(r.getEndTime())) {
			throw new IllegalArgumentException("startTime/endTime 不能为空");
		}
	}

	private static String midnightWarning(LocalDateTime start) {
		if (start.getHour() == 0 && start.getMinute() == 0 && start.getSecond() == 0) {
			return "\n注意：起始时间为当日 00:00:00，滑动窗口滤波可能边界不稳定，建议从 00:01:00 起选。\n";
		}
		return "";
	}

	private List<ChatMessageDto> trimHistory(AiAnalysisRequest request) {
		List<ChatMessageDto> h = request.getConversationHistory();
		if (h == null || h.isEmpty()) {
			return List.of();
		}
		int max = Math.max(0, aiProperties.getMaxConversationTurns());
		if (h.size() <= max) {
			return h;
		}
		return h.subList(h.size() - max, h.size());
	}

	private String buildFollowUpSection(AiAnalysisRequest request) {
		if (!StringUtils.hasText(request.getFollowUpQuestion())) {
			return "【用户当前无额外追问】";
		}
		return "【用户追问】\n" + request.getFollowUpQuestion().trim();
	}

	private static String formatCitationsForPrompt(List<CitationDto> citations) {
		if (citations.isEmpty()) {
			return "（无匹配知识库片段）";
		}
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (CitationDto c : citations) {
			sb.append("[").append(i++).append("] ").append(c.getTitle()).append("\n").append(c.getExcerpt()).append("\n\n");
		}
		return sb.toString();
	}

	private AiAnalysisResponse ruleOnlyResponse(TimeSeriesFeatureExtractor.PairFacts facts, AiAnalysisRequest request) {
		List<String> anomalies = TimeSeriesFeatureExtractor.ruleAnomalyNotes(facts);
		String summary = String.format("（离线模式，未调用大模型）表 %s 在窗口内共 %d 点，对比列 %s 与 %s。%s", request.getTableName(),
				facts.getY1().getN(), request.getY1Name(), request.getY2Name(),
				anomalies.isEmpty() ? "未触发显著规则。" : "已根据 3σ/阶跃规则给出提示。");
		return AiAnalysisResponse.builder()
			.summary(summary)
			.anomalyNotes(anomalies)
			.possibleCauses(List.of("需结合现场工况与传感器标定进一步确认。"))
			.actions(List.of("若已配置 OPENAI 兼容 API，可在配置中启用 demo.ai 以生成更自然的诊断建议。"))
			.confidence(0.55)
			.build();
	}

	private static void normalizeIfNoRealAnomaly(AiAnalysisResponse parsed, List<String> ruleAnomalies) {
		boolean ruleHasAnomaly = ruleAnomalies != null && !ruleAnomalies.isEmpty();
		if (ruleHasAnomaly) {
			return;
		}
		List<String> notes = Optional.ofNullable(parsed.getAnomalyNotes()).orElse(List.of());
		if (notes.isEmpty()) {
			return;
		}
		boolean allNeutral = notes.stream().allMatch(AiAnalysisServiceImpl::looksNeutralNote);
		if (!allNeutral) {
			return;
		}
		parsed.setAnomalyNotes(List.of());
		parsed.setPossibleCauses(List.of());
		parsed.setActions(List.of());
		if (!StringUtils.hasText(parsed.getSummary())) {
			parsed.setSummary("未见显著异常，整体波动平稳。");
		}
	}

	private static boolean looksNeutralNote(String s) {
		if (!StringUtils.hasText(s)) {
			return true;
		}
		String t = s.replace(" ", "");
		String[] neutral = { "无异常", "未见异常", "未触发", "平稳", "无突变", "无波动加剧", "无明显异常", "稳定偏移", "非异常" };
		for (String k : neutral) {
			if (t.contains(k)) {
				return true;
			}
		}
		return false;
	}

	private AiAnalysisResponse parseLlmJson(String raw) throws Exception {
		String json = extractJsonObject(raw);
		JsonNode n = objectMapper.readTree(json);
		return AiAnalysisResponse.builder()
			.summary(text(n, "summary"))
			.anomalyNotes(readList(n, "anomalyNotes"))
			.possibleCauses(readList(n, "possibleCauses"))
			.actions(readList(n, "actions"))
			.confidence(n.path("confidence").isNumber() ? n.path("confidence").asDouble() : 0.5)
			.build();
	}

	private static String extractJsonObject(String raw) {
		String t = raw.trim();
		int start = t.indexOf('{');
		int end = t.lastIndexOf('}');
		if (start >= 0 && end > start) {
			return t.substring(start, end + 1);
		}
		throw new IllegalArgumentException("无法解析模型 JSON");
	}

	private static String text(JsonNode n, String field) {
		JsonNode v = n.path(field);
		return v.isMissingNode() ? "" : v.asText("");
	}

	private static List<String> readList(JsonNode n, String field) {
		JsonNode arr = n.path(field);
		if (!arr.isArray()) {
			return new ArrayList<>();
		}
		List<String> out = new ArrayList<>();
		arr.forEach(x -> out.add(x.asText()));
		return out;
	}

	private static String buildPlainText(AiAnalysisResponse r) {
		String cite = "";
		if (r.getCitations() != null && !r.getCitations().isEmpty()) {
			cite = "\n\n【引用来源】\n" + r.getCitations()
				.stream()
				.map(c -> "- " + c.getTitle() + " (score=" + String.format(Locale.ROOT, "%.3f", c.getScore()) + ")")
				.collect(Collectors.joining("\n"));
		}
		return String.join("\n\n",
				"摘要: " + Optional.ofNullable(r.getSummary()).orElse(""),
				"异常提示: " + String.join("; ", Optional.ofNullable(r.getAnomalyNotes()).orElse(List.of())),
				"可能原因: " + String.join("; ", Optional.ofNullable(r.getPossibleCauses()).orElse(List.of())),
				"建议动作: " + String.join("; ", Optional.ofNullable(r.getActions()).orElse(List.of()))) + cite;
	}

}

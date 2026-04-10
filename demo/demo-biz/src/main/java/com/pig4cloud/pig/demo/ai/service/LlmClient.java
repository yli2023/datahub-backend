package com.pig4cloud.pig.demo.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pig4cloud.pig.demo.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容 Chat Completions 客户端。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmClient {

	private final HttpClient aiHttpClient;

	private final AiProperties aiProperties;

	private final ObjectMapper objectMapper;

	public String chatCompletions(List<Map<String, String>> messages) throws Exception {
		String base = aiProperties.getBaseUrl().replaceAll("/+$", "");
		String url = base + "/chat/completions";
		ObjectNode root = objectMapper.createObjectNode();
		root.put("model", aiProperties.getModel());
		root.put("temperature", aiProperties.getTemperature());
		ArrayNode arr = root.putArray("messages");
		for (Map<String, String> m : messages) {
			ObjectNode one = arr.addObject();
			one.put("role", m.get("role"));
			one.put("content", m.get("content"));
		}
		String body = objectMapper.writeValueAsString(root);
		HttpRequest req = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.timeout(Duration.ofSeconds(120))
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + aiProperties.getApiKey())
			.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
			.build();
		HttpResponse<String> resp = sendWithRetry(req);
		if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
			throw new IllegalStateException("LLM HTTP " + resp.statusCode() + ": " + resp.body());
		}
		JsonNode tree = objectMapper.readTree(resp.body());
		JsonNode content = tree.path("choices").path(0).path("message").path("content");
		if (content.isMissingNode() || content.asText().isBlank()) {
			throw new IllegalStateException("LLM 响应无 content: " + resp.body());
		}
		return content.asText();
	}

	private HttpResponse<String> sendWithRetry(HttpRequest req) throws Exception {
		Exception last = null;
		for (int i = 1; i <= 2; i++) {
			try {
				return aiHttpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			}
			catch (Exception e) {
				last = e;
				String msg = e.getMessage() == null ? "" : e.getMessage();
				boolean retryable = msg.contains("Connection reset") || msg.contains("connection reset");
				if (!retryable || i == 2) {
					throw e;
				}
				log.warn("LLM 调用第{}次失败，准备重试: {}", i, msg);
				Thread.sleep(300);
			}
		}
		throw last == null ? new IllegalStateException("LLM 请求失败") : last;
	}

}

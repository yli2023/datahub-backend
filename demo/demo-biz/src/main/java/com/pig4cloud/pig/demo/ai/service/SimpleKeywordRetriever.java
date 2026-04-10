package com.pig4cloud.pig.demo.ai.service;

import com.pig4cloud.pig.demo.ai.config.AiProperties;
import com.pig4cloud.pig.demo.ai.dto.CitationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 轻量 RAG：关键词重叠 + 简单 TF 权重，无外部向量服务依赖。
 */
@Service
@RequiredArgsConstructor
public class SimpleKeywordRetriever {

	private final KnowledgeBaseLoader loader;

	private final AiProperties aiProperties;

	public List<CitationDto> retrieve(String query) {
		if (query == null || query.isBlank()) {
			return List.of();
		}
		Set<String> qTokens = tokenize(query);
		if (qTokens.isEmpty()) {
			return List.of();
		}
		List<Scored> scored = new ArrayList<>();
		for (KnowledgeChunk c : loader.allChunks()) {
			Set<String> docTokens = tokenize(c.getTitle() + " " + c.getBody());
			double s = score(qTokens, docTokens, c.getBody().length());
			if (s > 0) {
				scored.add(new Scored(c, s));
			}
		}
		scored.sort(Comparator.comparingDouble(Scored::score).reversed());
		int topK = Math.max(1, aiProperties.getMaxKnowledgeChunks());
		return scored.stream().limit(topK).map(this::toCitation).collect(Collectors.toList());
	}

	private CitationDto toCitation(Scored s) {
		String ex = s.chunk().getBody();
		if (ex.length() > 400) {
			ex = ex.substring(0, 400) + "…";
		}
		return CitationDto.builder().id(s.chunk().getId()).title(s.chunk().getTitle()).excerpt(ex).score(s.score()).build();
	}

	private static double score(Set<String> q, Set<String> doc, int bodyLen) {
		if (doc.isEmpty()) {
			return 0;
		}
		int hit = 0;
		double idfSum = 0;
		for (String t : q) {
			if (doc.contains(t)) {
				hit++;
				idfSum += 1.0;
			}
		}
		if (hit == 0) {
			return 0;
		}
		double norm = Math.sqrt(doc.size()) * Math.log10(10 + bodyLen / 100.0);
		return idfSum / norm;
	}

	private static Set<String> tokenize(String s) {
		String[] parts = s.toLowerCase(Locale.ROOT).split("[^a-zA-Z0-9_\u4e00-\u9fa5]+");
		return Arrays.stream(parts).filter(t -> t.length() > 1).collect(Collectors.toCollection(HashSet::new));
	}

	private record Scored(KnowledgeChunk chunk, double score) {
	}

}

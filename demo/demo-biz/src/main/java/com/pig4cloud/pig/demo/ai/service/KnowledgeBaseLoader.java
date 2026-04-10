package com.pig4cloud.pig.demo.ai.service;

import com.pig4cloud.pig.demo.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 从 classpath 加载 Markdown 知识片段（按 ## 分块）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeBaseLoader {

	private final AiProperties aiProperties;

	private final List<KnowledgeChunk> chunks = new ArrayList<>();

	@PostConstruct
	public void load() {
		chunks.clear();
		String dir = aiProperties.getKnowledgeClasspath().replaceAll("^/+|/+$", "");
		String pattern = "classpath*:" + dir + "/**/*.md";
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = resolver.getResources(pattern);
		}
		catch (IOException e) {
			log.warn("Knowledge base scan failed: {}", e.getMessage());
			return;
		}
		AtomicInteger id = new AtomicInteger(1);
		for (Resource res : resources) {
			if (!res.isReadable()) {
				continue;
			}
			String path;
			String text;
			try {
				path = res.getURL().toString();
				text = res.getContentAsString(StandardCharsets.UTF_8);
			}
			catch (IOException e) {
				log.warn("Skip knowledge resource: {}", e.getMessage());
				continue;
			}
			chunks.addAll(splitMarkdown(res.getFilename() != null ? res.getFilename() : path, path, text, id));
		}
		log.info("Loaded {} knowledge chunks from {}", chunks.size(), pattern);
	}

	public List<KnowledgeChunk> allChunks() {
		return List.copyOf(chunks);
	}

	private static List<KnowledgeChunk> splitMarkdown(String fileTitle, String path, String text, AtomicInteger idGen) {
		List<KnowledgeChunk> out = new ArrayList<>();
		String[] parts = text.split("(?m)^##\\s+");
		if (parts.length <= 1) {
			out.add(new KnowledgeChunk("kb-" + idGen.getAndIncrement(), fileTitle, text.trim(), path));
			return out;
		}
		String head = parts[0].trim();
		if (!head.isEmpty()) {
			out.add(new KnowledgeChunk("kb-" + idGen.getAndIncrement(), fileTitle, head, path));
		}
		for (int i = 1; i < parts.length; i++) {
			String block = parts[i].trim();
			String title = fileTitle;
			String body = block;
			int nl = block.indexOf('\n');
			if (nl > 0) {
				title = block.substring(0, nl).trim();
				body = block.substring(nl + 1).trim();
			}
			out.add(new KnowledgeChunk("kb-" + idGen.getAndIncrement(), title, body, path));
		}
		return out;
	}

}

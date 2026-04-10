package com.pig4cloud.pig.demo.ai.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KnowledgeChunk {

	private String id;

	private String title;

	private String body;

	private String sourcePath;

}

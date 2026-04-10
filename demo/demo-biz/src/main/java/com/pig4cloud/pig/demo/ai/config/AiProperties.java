package com.pig4cloud.pig.demo.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 大模型与 RAG 相关配置（支持 OpenAI 兼容接口）。
 */
@Data
@ConfigurationProperties(prefix = "demo.ai")
public class AiProperties {

	/**
	 * 是否启用对外部 LLM 的调用；关闭时仅返回规则摘要 + 知识库引用。
	 */
	private boolean enabled = true;

	/**
	 * Chat Completions 基础 URL，例如 https://api.openai.com/v1
	 */
	private String baseUrl = "";

	/**
	 * API Key，建议使用环境变量注入，勿提交仓库。
	 */
	private String apiKey = "";

	private String model = "gpt-4o-mini";

	private double temperature = 0.2;

	/**
	 * 当前提示词模板版本（用于审计与 A/B）。
	 */
	private String promptVersion = "v1";

	/**
	 * 知识库 classpath 目录，例如 kb/
	 */
	private String knowledgeClasspath = "kb";

	private int maxKnowledgeChunks = 5;

	private int maxConversationTurns = 8;

}

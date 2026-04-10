package com.pig4cloud.pig.demo.ai.service;

import com.pig4cloud.pig.demo.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 提示词模板（版本化入口，便于评测对比）。
 */
@Service
@RequiredArgsConstructor
public class PromptTemplateService {

	private final AiProperties aiProperties;

	public String systemPrompt() {
		return """
				你是工业时序数据分析助手。用户会提供两张曲线在同一时间窗口内的统计特征、可选的追问以及知识库摘录。
				你必须：
				1) 仅基于给定事实与知识库作答，不要编造传感器物理含义。
				2) 输出**严格 JSON**，不要 Markdown 代码块，格式如下：
				{"summary":"...","anomalyNotes":["..."],"possibleCauses":["..."],"actions":["..."],"confidence":0.0}
				3) confidence 为 0~1；若信息不足，降低 confidence 并在 summary 中说明局限。
				4) 如果判断“无明确异常”，请把 anomalyNotes / possibleCauses / actions 输出为空数组 []，并在 summary 用中性描述（例如“未见显著异常，整体平稳”）。
				5) 如果用户追问与当前图表分析无关（例如闲聊、身份询问、与业务无关话题），请礼貌拒答并引导回图表：
				   - summary 给出简短拒答与引导（例如“我主要用于当前图表分析，请提问异常、差异、原因或排查建议”）
				   - anomalyNotes / possibleCauses / actions 输出为空数组 []
				   - confidence 建议 <= 0.4
				""";
	}

	public String userAnalysisPrompt(String factsText, String kbText, String followUpSection) {
		return "【统计特征与上下文】\n" + factsText + "\n\n【知识库摘录】\n" + kbText + "\n\n" + followUpSection;
	}

	public String version() {
		return aiProperties.getPromptVersion();
	}

}

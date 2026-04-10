package com.pig4cloud.pig.demo.ai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 与前端图表参数对齐的分析请求。
 */
@Data
public class AiAnalysisRequest {

	private String tableName;

	private String y1Name;

	private String y2Name;

	/** 格式 yyyy-MM-dd HH:mm:ss */
	private String startTime;

	private String endTime;

	/** 与 /demo/process 的 num 一致 */
	private Integer num;

	/** 追问（多轮） */
	private String followUpQuestion;

	private List<ChatMessageDto> conversationHistory = new ArrayList<>();

}

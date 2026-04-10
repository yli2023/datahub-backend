package com.pig4cloud.pig.demo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseMeta {

	private long latencyMs;

	private String promptVersion;

	private String model;

	private boolean usedLlm;

	private String traceId;

}

package com.pig4cloud.pig.demo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponse {

	private String summary;

	private List<String> anomalyNotes;

	private List<String> possibleCauses;

	private List<String> actions;

	private Double confidence;

	private List<CitationDto> citations;

	private String plainText;

	private AiResponseMeta meta;

	public static AiAnalysisResponse empty() {
		return AiAnalysisResponse.builder()
			.summary("")
			.anomalyNotes(new ArrayList<>())
			.possibleCauses(new ArrayList<>())
			.actions(new ArrayList<>())
			.confidence(0.0)
			.citations(new ArrayList<>())
			.plainText("")
			.build();
	}

}

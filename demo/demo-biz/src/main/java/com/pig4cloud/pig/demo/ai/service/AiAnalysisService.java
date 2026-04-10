package com.pig4cloud.pig.demo.ai.service;

import com.pig4cloud.pig.demo.ai.dto.AiAnalysisRequest;
import com.pig4cloud.pig.demo.ai.dto.AiAnalysisResponse;

public interface AiAnalysisService {

	AiAnalysisResponse analyze(AiAnalysisRequest request);

}

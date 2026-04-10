package com.pig4cloud.pig.demo.ai.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轻量调用审计与指标（内存聚合，适合演示与压测摘要）。
 */
@Service
public class AiAuditService {

	private final AtomicLong totalCalls = new AtomicLong();

	private final AtomicLong successCalls = new AtomicLong();

	private final AtomicLong failureCalls = new AtomicLong();

	private final AtomicLong llmCalls = new AtomicLong();

	private final AtomicLong totalLatencyMs = new AtomicLong();

	public String newTraceId() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
	}

	public void record(boolean success, boolean usedLlm, long latencyMs) {
		totalCalls.incrementAndGet();
		if (success) {
			successCalls.incrementAndGet();
		}
		else {
			failureCalls.incrementAndGet();
		}
		if (usedLlm) {
			llmCalls.incrementAndGet();
		}
		totalLatencyMs.addAndGet(Math.max(0, latencyMs));
	}

	public Map<String, Object> metricsSnapshot() {
		long total = totalCalls.get();
		long avg = total == 0 ? 0 : totalLatencyMs.get() / total;
		return Map.of("totalCalls", total, "successCalls", successCalls.get(), "failureCalls", failureCalls.get(), "llmCalls",
				llmCalls.get(), "avgLatencyMs", avg);
	}

}

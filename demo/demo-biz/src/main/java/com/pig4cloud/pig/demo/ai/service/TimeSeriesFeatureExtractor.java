package com.pig4cloud.pig.demo.ai.service;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 可解释规则特征：用于 LLM 上下文与无 LLM 时的降级摘要。
 */
public final class TimeSeriesFeatureExtractor {

	private TimeSeriesFeatureExtractor() {
	}

	@Value
	@Builder
	public static class SeriesFacts {
		String name;
		int n;
		double min;
		double max;
		double mean;
		double std;
		double maxStep;
		int spikeCount;
	}

	@Value
	@Builder
	public static class PairFacts {
		SeriesFacts y1;
		SeriesFacts y2;
		double meanAbsDiff;
		String timeSpanHuman;
	}

	public static PairFacts analyze(List<Date> times, List<Double> y1, List<Double> y2, String n1, String n2) {
		if (y1 == null || y2 == null || y1.size() != y2.size() || y1.isEmpty()) {
			throw new IllegalArgumentException("时序长度不一致或为空");
		}
		SeriesFacts f1 = single(n1, y1);
		SeriesFacts f2 = single(n2, y2);
		double mad = meanAbsDiff(y1, y2);
		String span = "";
		if (times != null && times.size() == y1.size()) {
			span = String.format(Locale.ROOT, "从 %s 到 %s", times.get(0), times.get(times.size() - 1));
		}
		return PairFacts.builder().y1(f1).y2(f2).meanAbsDiff(mad).timeSpanHuman(span).build();
	}

	public static List<String> ruleAnomalyNotes(PairFacts f) {
		List<String> notes = new ArrayList<>();
		if (f.getY1().getSpikeCount() > 0) {
			notes.add(String.format(Locale.ROOT, "序列「%s」检测到约 %d 处相对均值显著偏离的点（3σ 规则）。", f.getY1().getName(),
					f.getY1().getSpikeCount()));
		}
		if (f.getY2().getSpikeCount() > 0) {
			notes.add(String.format(Locale.ROOT, "序列「%s」检测到约 %d 处相对均值显著偏离的点（3σ 规则）。", f.getY2().getName(),
					f.getY2().getSpikeCount()));
		}
		if (f.getY1().getMaxStep() > 3 * Math.max(1e-6, f.getY1().getStd())) {
			notes.add(String.format(Locale.ROOT, "「%s」存在较大阶跃（单步变化 %.4f）。", f.getY1().getName(), f.getY1().getMaxStep()));
		}
		if (f.getY2().getMaxStep() > 3 * Math.max(1e-6, f.getY2().getStd())) {
			notes.add(String.format(Locale.ROOT, "「%s」存在较大阶跃（单步变化 %.4f）。", f.getY2().getName(), f.getY2().getMaxStep()));
		}
		if (notes.isEmpty()) {
			notes.add("在当前窗口内未触发显著异常规则（3σ/阶跃），曲线整体相对平稳。");
		}
		return notes;
	}

	public static String toFactsText(PairFacts f) {
		return String.format(Locale.ROOT,
				"时间范围: %s\n点数: %d\n"
						+ "[%s] min=%.4f max=%.4f mean=%.4f std=%.4f maxStep=%.4f spikes~%d\n"
						+ "[%s] min=%.4f max=%.4f mean=%.4f std=%.4f maxStep=%.4f spikes~%d\n"
						+ "两序列逐点绝对差均值: %.4f\n",
				f.getTimeSpanHuman(), f.getY1().getN(), f.getY1().getName(), f.getY1().getMin(), f.getY1().getMax(),
				f.getY1().getMean(), f.getY1().getStd(), f.getY1().getMaxStep(), f.getY1().getSpikeCount(),
				f.getY2().getName(), f.getY2().getMin(), f.getY2().getMax(), f.getY2().getMean(), f.getY2().getStd(),
				f.getY2().getMaxStep(), f.getY2().getSpikeCount(), f.getMeanAbsDiff());
	}

	private static SeriesFacts single(String name, List<Double> y) {
		int n = y.size();
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		double sum = 0;
		for (double v : y) {
			min = Math.min(min, v);
			max = Math.max(max, v);
			sum += v;
		}
		double mean = sum / n;
		double var = 0;
		for (double v : y) {
			double d = v - mean;
			var += d * d;
		}
		double std = Math.sqrt(var / Math.max(1, n - 1));
		double maxStep = 0;
		for (int i = 1; i < n; i++) {
			maxStep = Math.max(maxStep, Math.abs(y.get(i) - y.get(i - 1)));
		}
		int spikes = 0;
		double thr = mean + 3 * Math.max(1e-9, std);
		double thrLow = mean - 3 * Math.max(1e-9, std);
		for (double v : y) {
			if (v > thr || v < thrLow) {
				spikes++;
			}
		}
		return SeriesFacts.builder().name(name).n(n).min(min).max(max).mean(mean).std(std).maxStep(maxStep).spikeCount(spikes).build();
	}

	private static double meanAbsDiff(List<Double> a, List<Double> b) {
		double s = 0;
		for (int i = 0; i < a.size(); i++) {
			s += Math.abs(a.get(i) - b.get(i));
		}
		return s / a.size();
	}

}

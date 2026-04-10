package com.pig4cloud.pig.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import com.pig4cloud.pig.demo.mapper.DemoMapper;
import com.pig4cloud.pig.demo.service.DemoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * demo 表
 *
 * @author pig
 * @date 2024-07-30 14:22:46
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements DemoService {

	private final DemoMapper demoMapper;

	public DemoServiceImpl(DemoMapper demoMapper) {
		this.demoMapper = demoMapper;
	}

	public List<String> getColumnNames(String tableName) {
		return demoMapper.getColumnNames(tableName);
	}

	public Long findIdByCreateTime(LocalDateTime createTime, String tableName) {
		return demoMapper.findIdByCreateTime(createTime, tableName);
	}

	public List<Double> selectColumn(String columnName, String tableName) {
		return demoMapper.selectColumn(columnName, tableName);
	}

	public List<Date> selectTime(String columnName, String tableName) {
		return demoMapper.selectTime(columnName, tableName);
	}

	public Long getFirstRecordId(String tableName) {
		return demoMapper.getFirstRecordId(tableName);
	}

	@Override
	public List<Double> getProcessedColumnSlice(String tableName, LocalDateTime startTime, LocalDateTime endTime,
			String columnName, int num) {
		Long header = demoMapper.findIdByCreateTime(startTime, tableName) - num;
		Long bottom = demoMapper.findIdByCreateTime(endTime, tableName) - num;
		List<Double> res = demoMapper.selectColumn(columnName, tableName);
		Long firstRecord = demoMapper.getFirstRecordId(tableName);
		Queue<Double> window = new LinkedList<>();
		int windowSize = 3;
		long listSize = bottom - header + windowSize;
		double[] data = new double[(int) listSize];
		for (int i = 0; i < listSize; i++) {
			data[i] = res.get((int) (header - windowSize + 1 + i - firstRecord));
		}
		double[] filteredData = new double[(int) listSize];
		for (int i = 0; i < listSize; i++) {
			filteredData[i] = slidingAverageFilter(data[i], windowSize, window);
		}
		for (int i = 0; i < bottom - header + 1; i++) {
			res.set((int) (header - firstRecord + i), filteredData[windowSize - 1 + i]);
		}
		return res.subList((int) (header - firstRecord), (int) (bottom + 1 - firstRecord));
	}

	@Override
	public List<Date> getCreateTimeSlice(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
		Long header = demoMapper.findIdByCreateTime(startTime, tableName);
		Long bottom = demoMapper.findIdByCreateTime(endTime, tableName);
		Long firstRecord = demoMapper.getFirstRecordId(tableName);
		List<Date> res = demoMapper.selectTime("create_time", tableName);
		return res.subList((int) (header - firstRecord), (int) (bottom + 1 - firstRecord));
	}

	private static double slidingAverageFilter(double value, int windowSize, Queue<Double> window) {
		window.add(value);
		if (window.size() > windowSize) {
			window.poll();
		}
		int sum = 0;
		for (double n : window) {
			sum += (int) n;
		}
		return sum / (double) window.size();
	}
}

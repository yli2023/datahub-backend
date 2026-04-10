package com.pig4cloud.pig.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.demo.entity.DemoEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface DemoService extends IService<DemoEntity> {
	List<String> getColumnNames(String tableName);
	Long findIdByCreateTime(LocalDateTime createTime, String tableName);
	List<Double> selectColumn(String columnName, String tableName);
	List<Date> selectTime(String columnName, String tableName);
	Long getFirstRecordId(String tableName);

	/**
	 * 与图表接口一致的滑动平均滤波后，时间区间内某列数值切片。
	 */
	List<Double> getProcessedColumnSlice(String tableName, LocalDateTime startTime, LocalDateTime endTime,
			String columnName, int num);

	/**
	 * 时间区间内 create_time 切片（与 /demo/select 一致）。
	 */
	List<Date> getCreateTimeSlice(String tableName, LocalDateTime startTime, LocalDateTime endTime);
}

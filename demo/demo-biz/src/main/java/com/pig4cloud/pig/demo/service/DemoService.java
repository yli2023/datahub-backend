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
}

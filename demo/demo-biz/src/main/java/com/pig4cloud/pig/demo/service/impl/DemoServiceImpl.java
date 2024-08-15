package com.pig4cloud.pig.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import com.pig4cloud.pig.demo.mapper.DemoMapper;
import com.pig4cloud.pig.demo.service.DemoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
}

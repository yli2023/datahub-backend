package com.pig4cloud.pig.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {
	@Select("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'pig_demo' AND TABLE_NAME = #{tableName}")
	List<String> getColumnNames(@Param("tableName") String tableName);

	@Select("SELECT ID FROM ${tableName} WHERE CREATE_TIME = #{createTime}")
	Long findIdByCreateTime(@Param("createTime") LocalDateTime createTime, @Param("tableName") String tableName);

	@Select("SELECT ${columnName} FROM ${tableName}")
	List<Double> selectColumn(@Param("columnName") String columnName, @Param("tableName") String tableName);

	@Select("SELECT ${columnName} FROM ${tableName}")
	List<Date> selectTime(@Param("columnName") String columnName, @Param("tableName") String tableName);

	@Select("SELECT ID FROM ${tableName} LIMIT 1")
	Long getFirstRecordId(@Param("tableName") String tableName);
}

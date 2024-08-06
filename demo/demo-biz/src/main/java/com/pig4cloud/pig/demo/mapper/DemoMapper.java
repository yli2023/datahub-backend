package com.pig4cloud.pig.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {
	@Select("SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'pig_demo' AND TABLE_NAME = #{tableName}")
	List<String> getColumnNames(@Param("tableName") String tableName);
}

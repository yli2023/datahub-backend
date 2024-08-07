package com.pig4cloud.pig.demo.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.demo.mapper.DemoMapper;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import com.pig4cloud.pig.demo.service.DemoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.apache.ibatis.annotations.Param;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

//import static com.pig4cloud.pig.demo.mapper.DemoMapper.windowSize;

/**
 * demo 表
 *
 * @author pig
 * @date 2024-07-30 14:22:46
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/demo" )
@Tag(description = "demo" , name = "demo 表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DemoController {

    private final  DemoService demoService;
	private final DemoMapper demoMapper;

    /**
     * 分页查询
     * @param page 分页对象
     * @param demo demo 表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("demo_demo_view")
    public R getDemoPage(@ParameterObject Page page, @ParameterObject DemoEntity demo) {
        LambdaQueryWrapper<DemoEntity> wrapper = Wrappers.lambdaQuery(demo);
        return R.ok(demoService.page(page, wrapper));
    }


    /**
     * 通过条件查询demo 表
     * @param demo 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("demo_demo_view")
    public R getDetails(@ParameterObject DemoEntity demo) {
        return R.ok(demoService.list(Wrappers.query(demo)));
    }

    /**
     * 新增demo 表
     * @param demo demo 表
     * @return R
     */
    @Operation(summary = "新增demo 表" , description = "新增demo 表" )
    @SysLog("新增demo 表" )
    @PostMapping
    @HasPermission("demo_demo_add")
    public R save(@RequestBody DemoEntity demo) {
        return R.ok(demoService.save(demo));
    }

    /**
     * 修改demo 表
     * @param demo demo 表
     * @return R
     */
    @Operation(summary = "修改demo 表" , description = "修改demo 表" )
    @SysLog("修改demo 表" )
    @PutMapping
    @HasPermission("demo_demo_edit")
    public R updateById(@RequestBody DemoEntity demo) {
        return R.ok(demoService.updateById(demo));
    }

    /**
     * 通过id删除demo 表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除demo 表" , description = "通过id删除demo 表" )
    @SysLog("通过id删除demo 表" )
    @DeleteMapping
    @HasPermission("demo_demo_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(demoService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param demo 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("demo_demo_export")
    public List<DemoEntity> export(DemoEntity demo,Long[] ids) {
        return demoService.list(Wrappers.lambdaQuery(demo).in(ArrayUtil.isNotEmpty(ids), DemoEntity::getId, ids));
    }

	/**
	 * 数据库的部分时间返回前端
	 * <p>
	 * 所有列均显示
	 */
	@Operation(summary = "数据库的部分时间返回前端" , description = "数据库的部分时间返回前端" )
	@GetMapping("/select" )
	@HasPermission("demo_demo_view")
	public R getSelect(@RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime")LocalDateTime endTime) {
		Integer header= demoMapper.findIdByCreateTime(startTime, "demo");
		Integer bottom= demoMapper.findIdByCreateTime(endTime, "demo");
//		wrapper.like("username", "1").lt("id", 40).select("id","name");
//		wrapper.gt(DemoEntity::getId, 40).select(DemoEntity::getUsername, DemoEntity::getNicename);
		List<Date> res = demoMapper.selectTime("create_time", "demo");
		//截取需要的数
		List<Date> subList = res.subList(header, bottom + 1);
		return R.ok(subList);
	}

	/**
	 * 将滤波后的一列数据返回前端
	 * <p>
	 * 所有列均显示
	 */
	@Operation(summary = "滤波" , description = "滤波" )
	@GetMapping("/process" )
	@HasPermission("demo_demo_view")
	public R getProcess(@RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime")LocalDateTime endTime, @RequestParam("columnName")String columnName) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date dateTime1 = dateFormat.parse(vary3);
//		Date dateTime2 = dateFormat.parse(vary4);
		Integer header= demoMapper.findIdByCreateTime(startTime, "demo");
		Integer bottom= demoMapper.findIdByCreateTime(endTime, "demo");
		List<Double> res = demoMapper.selectColumn(columnName, "demo");

		Queue<Double> window = new LinkedList<>();
		int windowSize = 5; // 滑动窗口的大小
		// 提取数据列
		double[] data = new double[res.size()];
		for (int i = 0; i < res.size(); i++) {
			data[i] = res.get(i);
		}

		double[] filteredData = new double[res.size()];
		
		// 应用滑动平均滤波
		for(int i = 0; i < res.size(); i++){
			filteredData[i] = slidingAverageFilter(data[i], windowSize, window);
		}

		// 更新平滑后的值到对象
		for (int i = 0; i < res.size(); i++) {
			res.set(i, filteredData[i]);
		}

		//截取需要的数
		List<Double> subList = res.subList(header, bottom + 1);

		return R.ok(subList);
	}

	private double slidingAverageFilter(double value, int windowSize, Queue<Double> window) {
		// 添加当前值到滑动窗口中
		window.add(value);

		// 如果滑动窗口的大小超过了指定大小，移除最旧的元素
		if (window.size() > windowSize) {
			window.poll();
		}

		// 计算滑动窗口中所有元素的平均值
		int sum = 0;
		for (double num : window) {
			sum += (int) num;
		}
		int average = sum / window.size();

		return average;
	}

	/**
	 * 查询结果只返回两列(显示)
	 * <p>
	 * 只显示id、name 两列
	 */
	@Operation(summary = "按列查询demo 表" , description = "按列查询demo 表" )
	@GetMapping("/select1" )
	@HasPermission("demo_demo_view")
	public R getSelect(@ParameterObject Page page, @ParameterObject DemoEntity demo) {
		LambdaQueryWrapper<DemoEntity> wrapper = Wrappers.lambdaQuery(demo);
		wrapper.select(DemoEntity::getId, DemoEntity::getUsername);
		return R.ok(demoService.page(page, wrapper));
	}

	/**
	 * 列名接口
	 * <p>
	 * 返回列名
	 */
	@Operation(summary = "返回列名" , description = "返回列名" )
	@GetMapping("/column" )
	@HasPermission("demo_demo_view")
//	public R getColumn(String tableName) {
//		List<String> res = demoMapper.getColumnNames(tableName);
//		return R.ok(res);
//	}
		public R getColumn() {
		List<String> res = demoMapper.getColumnNames("demo");
		return R.ok(res);
	}

	/**
	 * 查询结果只返回时间
	 * <p>
	 * 只显示create_time列
	 */
	@Operation(summary = "查询结果只返回时间" , description = "查询结果只返回时间" )
	@GetMapping("/time" )
	@HasPermission("demo_demo_view")
	public R getTime() {
		List<Date> res = demoMapper.selectTime("create_time", "demo");
		return R.ok(res);
	}
}

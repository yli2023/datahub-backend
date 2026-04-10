package com.pig4cloud.pig.demo.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.pig.demo.ai.dto.AiAnalysisRequest;
import com.pig4cloud.pig.demo.ai.dto.AiAnalysisResponse;
import com.pig4cloud.pig.demo.ai.service.AiAnalysisService;
import com.pig4cloud.pig.demo.ai.service.AiAuditService;
import com.pig4cloud.pig.demo.entity.DemoEntity;
import com.pig4cloud.pig.demo.service.DemoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	private final AiAnalysisService aiAnalysisService;

	private final AiAuditService aiAuditService;

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
	public R getSelect(@RequestParam("tableName") String tableName, @RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime")LocalDateTime endTime) {
		return R.ok(demoService.getCreateTimeSlice(tableName, startTime, endTime));
	}

	/**
	 * 将滤波且数据对齐后的一列数据返回前端
	 * <p>
	 * 所有列均显示
	 */
	@Operation(summary = "滤波" , description = "滤波" )
	@GetMapping("/process" )
	@HasPermission("demo_demo_view")
	public R getProcess(@RequestParam("tableName") String tableName, @RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime")LocalDateTime endTime, @RequestParam("columnName")String columnName, @RequestParam("num") Integer num) {
		return R.ok(demoService.getProcessedColumnSlice(tableName, startTime, endTime, columnName, num));
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
		public R getColumn(@RequestParam("tableName") String tableName) {
		List<String> res = demoService.getColumnNames(tableName);
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
	public R getTime(@RequestParam("tableName") String tableName) {
		List<Date> res = demoService.selectTime("create_time", tableName);
		return R.ok(res);
	}

	@Operation(summary = "图表上下文智能分析", description = "统计特征 + 知识库 + 可选 LLM（与 /demo 同控制器，避免路由遗漏）")
	@PostMapping("/ai/analyze")
	@HasPermission("demo_demo_view")
	public R<AiAnalysisResponse> aiAnalyze(@RequestBody AiAnalysisRequest request) {
		return R.ok(aiAnalysisService.analyze(request));
	}

	@Operation(summary = "AI 调用指标", description = "内存聚合")
	@GetMapping("/ai/metrics")
	@HasPermission("demo_demo_view")
	public R<Map<String, Object>> aiMetrics() {
		return R.ok(aiAuditService.metricsSnapshot());
	}
}

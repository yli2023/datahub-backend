package com.pig4cloud.pig.demo.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.pig.demo.entity.ControlEntity;
import com.pig4cloud.pig.demo.service.ControlService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * control 表
 *
 * @author pig
 * @date 2024-08-05 08:57:24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/control" )
@Tag(description = "control" , name = "control 表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ControlController {

    private final  ControlService controlService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param control control 表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("demo_control_view")
    public R getControlPage(@ParameterObject Page page, @ParameterObject ControlEntity control) {
        LambdaQueryWrapper<ControlEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(controlService.page(page, wrapper));
    }


    /**
     * 通过条件查询control 表
     * @param control 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("demo_control_view")
    public R getDetails(@ParameterObject ControlEntity control) {
        return R.ok(controlService.list(Wrappers.query(control)));
    }

    /**
     * 新增control 表
     * @param control control 表
     * @return R
     */
    @Operation(summary = "新增control 表" , description = "新增control 表" )
    @SysLog("新增control 表" )
    @PostMapping
    @HasPermission("demo_control_add")
    public R save(@RequestBody ControlEntity control) {
        return R.ok(controlService.save(control));
    }

    /**
     * 修改control 表
     * @param control control 表
     * @return R
     */
    @Operation(summary = "修改control 表" , description = "修改control 表" )
    @SysLog("修改control 表" )
    @PutMapping
    @HasPermission("demo_control_edit")
    public R updateById(@RequestBody ControlEntity control) {
        return R.ok(controlService.updateById(control));
    }

    /**
     * 通过id删除control 表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除control 表" , description = "通过id删除control 表" )
    @SysLog("通过id删除control 表" )
    @DeleteMapping
    @HasPermission("demo_control_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(controlService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param control 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("demo_control_export")
    public List<ControlEntity> export(ControlEntity control,Long[] ids) {
        return controlService.list(Wrappers.lambdaQuery(control).in(ArrayUtil.isNotEmpty(ids), ControlEntity::getId, ids));
    }
}

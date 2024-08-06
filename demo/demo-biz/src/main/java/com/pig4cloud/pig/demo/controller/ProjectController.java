package com.pig4cloud.pig.demo.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.pig.demo.entity.ProjectEntity;
import com.pig4cloud.pig.demo.service.ProjectService;

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
 * project 表
 *
 * @author pig
 * @date 2024-07-31 11:14:31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project" )
@Tag(description = "project" , name = "project 表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ProjectController {

    private final  ProjectService projectService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param project project 表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("demo_project_view")
    public R getProjectPage(@ParameterObject Page page, @ParameterObject ProjectEntity project) {
        LambdaQueryWrapper<ProjectEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(projectService.page(page, wrapper));
    }


    /**
     * 通过条件查询project 表
     * @param project 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("demo_project_view")
    public R getDetails(@ParameterObject ProjectEntity project) {
        return R.ok(projectService.list(Wrappers.query(project)));
    }

    /**
     * 新增project 表
     * @param project project 表
     * @return R
     */
    @Operation(summary = "新增project 表" , description = "新增project 表" )
    @SysLog("新增project 表" )
    @PostMapping
    @HasPermission("demo_project_add")
    public R save(@RequestBody ProjectEntity project) {
        return R.ok(projectService.save(project));
    }

    /**
     * 修改project 表
     * @param project project 表
     * @return R
     */
    @Operation(summary = "修改project 表" , description = "修改project 表" )
    @SysLog("修改project 表" )
    @PutMapping
    @HasPermission("demo_project_edit")
    public R updateById(@RequestBody ProjectEntity project) {
        return R.ok(projectService.updateById(project));
    }

    /**
     * 通过id删除project 表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除project 表" , description = "通过id删除project 表" )
    @SysLog("通过id删除project 表" )
    @DeleteMapping
    @HasPermission("demo_project_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(projectService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param project 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("demo_project_export")
    public List<ProjectEntity> export(ProjectEntity project,Long[] ids) {
        return projectService.list(Wrappers.lambdaQuery(project).in(ArrayUtil.isNotEmpty(ids), ProjectEntity::getId, ids));
    }
}

package com.pig4cloud.pig.demo.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.pig.demo.entity.FileEntity;
import com.pig4cloud.pig.demo.service.FileService;

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
 * file 表
 *
 * @author pig
 * @date 2024-07-31 16:29:45
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file" )
@Tag(description = "file" , name = "file 表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class FileController {

    private final  FileService fileService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param file file 表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("demo_file_view")
    public R getFilePage(@ParameterObject Page page, @ParameterObject FileEntity file) {
        LambdaQueryWrapper<FileEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(fileService.page(page, wrapper));
    }


    /**
     * 通过条件查询file 表
     * @param file 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("demo_file_view")
    public R getDetails(@ParameterObject FileEntity file) {
        return R.ok(fileService.list(Wrappers.query(file)));
    }

    /**
     * 新增file 表
     * @param file file 表
     * @return R
     */
    @Operation(summary = "新增file 表" , description = "新增file 表" )
    @SysLog("新增file 表" )
    @PostMapping
    @HasPermission("demo_file_add")
    public R save(@RequestBody FileEntity file) {
        return R.ok(fileService.save(file));
    }

    /**
     * 修改file 表
     * @param file file 表
     * @return R
     */
    @Operation(summary = "修改file 表" , description = "修改file 表" )
    @SysLog("修改file 表" )
    @PutMapping
    @HasPermission("demo_file_edit")
    public R updateById(@RequestBody FileEntity file) {
        return R.ok(fileService.updateById(file));
    }

    /**
     * 通过id删除file 表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除file 表" , description = "通过id删除file 表" )
    @SysLog("通过id删除file 表" )
    @DeleteMapping
    @HasPermission("demo_file_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(fileService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param file 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("demo_file_export")
    public List<FileEntity> export(FileEntity file,Long[] ids) {
        return fileService.list(Wrappers.lambdaQuery(file).in(ArrayUtil.isNotEmpty(ids), FileEntity::getId, ids));
    }
}

package com.pig4cloud.pig.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * file 表
 *
 * @author pig
 * @date 2024-07-31 16:29:45
 */
@Data
@TableName("file")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "file 表")
public class FileEntity extends Model<FileEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 文件名
	*/
    @Schema(description="文件名")
    private String filename;

	/**
	* 文件类型
	*/
    @Schema(description="文件类型")
    private String filetype;

	/**
	* 文件大小
	*/
    @Schema(description="文件大小")
    private String filecapacity;

	/**
	* 删除标识
	*/
    @Schema(description="删除标识")
    private Integer flag;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 更新人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新人")
    private String updateBy;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;
}

package com.pig4cloud.pig.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * demo 表
 *
 * @author pig
 * @date 2024-07-30 14:22:46
 */
@Data
@TableName("demo")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "demo 表")
public class DemoEntity extends Model<DemoEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 用户名
	*/
    @Schema(description="用户名")
    private String username;

	/**
	* 昵称
	*/
    @Schema(description="昵称")
    private String nicename;

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
	* 修改时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改时间")
    private LocalDateTime updateTime;
}

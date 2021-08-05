package com.touchealth.platform.processengine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Description:基础类
 *
 * @author admin
 * @date 2020/10/28
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 未设置主键生成策略时，默认采用雪花算法
     */
    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    /**
     * 创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 更新人id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标记 0未删除 1删除
     */
    @TableLogic
    @TableField(select = false)
    private Integer deletedFlag;

}

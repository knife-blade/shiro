package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单管理组件表
 * </p>
 *
 * @author lvx
 * @since 2021-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_order_management")
public class OrderManagement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 组件所在的渠道编码
     */
    private String channelNo;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 组件所在的页面ID
     */
    private Long pageId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     */
    private Integer status;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    /**
     * 版本号ID
     */
    private Long version;


}

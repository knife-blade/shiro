package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.touchealth.platform.processengine.constant.*;

/**
 * <p>
 * 按钮通用组件表
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_btn")
public class Btn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 按钮组件所在的渠道号
     */
    private String channelNo;

    /**
     * 按钮组ID
     */
    private Long btnGroupId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     * @see CommonConstant.STATUS
     */
    private Integer status;

    /**
     * 按钮名称
     */
    private String name;

    /**
     * 按钮背图地址
     */
    private String bgUrl;

    /**
     * 顺序编号。值越小越靠前，值越大余额靠后。
     */
    private Integer sort;

    /**
     * 链接组件ID
     */
    private Long linkModuleId;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     * <b color=red>该字段暂不使用</b>
     */
    private String webJson;

    /**
     * 版本号ID
     */
    private Long version;


}

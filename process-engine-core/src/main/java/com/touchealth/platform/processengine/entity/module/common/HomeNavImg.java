package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 首页导航栏图片链接配置表
 * </p>
 *
 * @author liqone
 * @since 2021-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_home_nav_img")
public class HomeNavImg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 轮播图图片名称
     */
    private String name;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 轮播图组件所在的渠道编码
     */
    private String channelNo;

    /**
     * 底部导航组件ID
     */
    private Long homeNavId;

    /**
     * 链接组件ID
     */
    private Long linkModuleId;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    /**
     * 顺序编号。值越小越靠前，值越大余额靠后。
     */
    private Integer sort;

    /**
     * 版本号ID
     */
    private Long version;


}

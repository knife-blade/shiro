package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 坑位导航通用组件表之图片内容表
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_navigate_img")
public class NavigateImg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 坑位导航图片名称
     */
    private String name;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 坑位导航组件所在的渠道ID
     */
    private String channelNo;

    /**
     * 坑位导航组件所在的页面ID
     */
    private Long pageId;

    /**
     * 坑位导航组件ID
     */
    private Long navigateId;

    /**
     * 链接组件ID
     */
    private Long linkModuleId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     */
    private Integer status;

    /**
     * 顺序编号。值越小越靠前，值越大余额靠后。
     */
    private Integer sort;

    /**
     * 版本号ID
     */
    private Long version;


}

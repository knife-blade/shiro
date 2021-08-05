package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 热区通用组件表之热区部分表
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_hotspot_parts")
public class HotspotParts extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 热区名称
     */
    private String name;

    /**
     * 热区组件所在的渠道ID
     */
    private String channelNo;

    /**
     * 热区组件所在的页面ID
     */
    private Long pageId;

    /**
     * 热区组件ID
     */
    private Long hotspotId;

    /**
     * 链接组件ID
     */
    private Long linkModuleId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     */
    private Integer status;

    /**
     * 热区绘制信息。有前端提供记录在此，原样返回前端。
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

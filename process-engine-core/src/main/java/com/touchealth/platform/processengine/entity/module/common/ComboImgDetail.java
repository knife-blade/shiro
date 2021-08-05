package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 组合图通用组件表之图片内容表
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_combo_img_detail")
public class ComboImgDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 组合图中的图片名称
     */
    private String name;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 组合图组件所在的渠道ID
     */
    private String channelNo;

    /**
     * 组合图组件所在的页面ID
     */
    private Long pageId;

    /**
     * 组合图组件所在的上级组件ID
     */
    private Long mParentId;

    /**
     * 组合图组件ID
     */
    private Long comboImgId;

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

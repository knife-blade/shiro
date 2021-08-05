package com.touchealth.platform.processengine.entity.module.common;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 页面配置模板
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。
     */
    private String webJson;

    /**
     * 预览图片链接
     */
    private String previewImg;

    /**
     * 模版类型，与RouterName枚举值对应
     */
    private String type;

}

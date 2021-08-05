package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

/**
 * <p>
 * 页面配置模板
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@Data
public class PageTemplateDto{

    private Long id;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。
     */
    private String webJson;

    /**
     * 预览图片链接
     */
    private String previewImg;

}

package com.touchealth.platform.processengine.pojo.request.page;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author liufengqiang
 * @date 2021-04-26 09:36:04
 */
@Data
public class PresetPageRequest {

    /**
     * 前端页面配置信息
     */
    @NotBlank(message = "配置信息不能为空")
    private String webJson;
    /**
     * 页面类型 0.入口页面 1.非入口页面（不展示在页面列表）
     */
    private Integer pageType;
}

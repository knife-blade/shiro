package com.touchealth.platform.processengine.pojo.request.page;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 页面应用模板请求参数
 * @author liqone
 * @date 2020-11-17 15:05:49
 */
@Data
public class ApplyPageTemplateRequest {

    /**
     * 对应的模板id
     */
    @NotNull(message = "模板id不能为空")
    private Long templateId;
}

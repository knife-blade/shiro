package com.touchealth.platform.processengine.pojo.request.module.common;

import lombok.Data;

/**
 * 查询渠道对应的首页导航组件参数
 */
@Data
public class HomeNavRequest {
    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。
     */
    private String webJson;
}

package com.touchealth.platform.processengine.pojo.bo.module.common;

import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import lombok.Data;

@Data
public class HotspotPartsBo extends BaseDto {
    /**
     * 热区名
     */
    private String name;
    /**
     * 顺序。值越小，越靠前。
     */
    private Integer sort;
    /**
     * 链接组件
     */
    private LinkDto linkDto;
    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    /**
     * 热区位置
     */
    private WebJsonBo.HotspotPartsBo style;
}

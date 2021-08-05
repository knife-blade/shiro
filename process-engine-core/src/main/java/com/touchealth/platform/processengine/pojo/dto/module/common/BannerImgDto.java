package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BannerImgDto extends BaseDto {
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片链接
     */
    private String url;
    /**
     * 链接组件ID
     */
    private Long linkModuleId;
    /**
     * 投放开始时间
     */
    private LocalDateTime showStartTime;
    /**
     * 投放结束时间
     */
    private LocalDateTime showEndTime;
    /**
     * 排序越小越靠前
     */
    private Integer sort;

    private Long bannerId;

    private Long componentId;

    private String pageName;

    private Long pageId;

    private String version;

    private Long versionId;

    private LinkDto linkDto;

    private LocalDateTime updatedTime;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    private Integer status;
}

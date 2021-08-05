package com.touchealth.platform.processengine.pojo.request.module.common;

import lombok.Data;

@Data
public class HotspotRequest {
    /**
     * 页面名称
     */
    private String pageName;
    /**
     * 页面ID
     */
    private Long pageId;
    /**
     * 渠道编码
     */
    private String channelNo;
    /**
     * 版本号
     */
    private String version;
    /**
     * 标题名称
     */
    private String name;

    /**
     * 回收状态：0：未回收 1：已回收
     */
    private Integer recycleStatus;

    Integer pageNo = 1;

    Integer pageSize = 10;
}

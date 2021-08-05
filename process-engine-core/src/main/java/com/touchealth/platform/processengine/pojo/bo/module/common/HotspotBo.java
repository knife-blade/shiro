package com.touchealth.platform.processengine.pojo.bo.module.common;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

import java.util.List;

@Data
public class HotspotBo extends BaseDto{
    /**
     * 渠道ID
     */
    private String channelNo;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String url;
    /**
     * 页面ID
     */
    private Long pageId;
    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     * @see CommonConstant.STATUS
     */
    private Integer status;
    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;
    /**
     * 版本号ID
     */
    private Long version;
    /**
     * 版本号
     */
    private String versionName;
    /**
     * 热区图
     */
    List<HotspotPartsBo> hotspotPartsBos;
}

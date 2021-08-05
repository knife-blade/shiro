package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

import java.util.List;

@Data
public class ComboImgDto extends BaseDto{
    /**
     * 热区组件所在的渠道编码
     */
    private String channelNo;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 热区组件所在的页面ID
     */
    private Long pageId;

    private String pageName;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    /**
     * 版本号
     */
    private Long versionId;

    /**
     * 组合图片
     */
    List<ComboImgDetailDto> comboImgDetailDtos;
}

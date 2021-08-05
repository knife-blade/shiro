package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

/**
 * 分隔符组件信息
 */
@Data
public class DelimiterDto extends BaseDto {

    /**
     * 分隔符组件所在的渠道号
     */
    private String channelNo;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 分隔符组件所在的页面ID
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
     * 分隔符名称
     */
    private String name;

    /**
     * 版本号ID
     */
    private Long version;

}

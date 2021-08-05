package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import com.touchealth.platform.processengine.entity.module.common.Link;
import lombok.Data;

/**
 * 按钮信息
 */
@Data
public class BtnDto extends BaseDto {

    /**
     * 按钮名
     */
    private String name;
    /**
     * 按钮背景图片。扩展字段，可能用不到。
     */
    private String bgUrl;
    /**
     * 顺序。值越小，越靠前。
     */
    private Integer sort;
    /**
     * 链接组件ID
     * @see Link#getId()
     */
    private Long linkModuleId;
    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     * <b color=red>该字段暂不使用</b>
     */
    private String webJson;

}

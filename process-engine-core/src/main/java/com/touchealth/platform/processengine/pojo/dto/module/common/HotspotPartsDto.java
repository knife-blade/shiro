package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotspotPartsDto extends BaseDto {
    /**
     * 图片名称
     */
    private String name;


    private String url;

    /**
     * 链接组件ID
     */
    private Long linkModuleId;

    /**
     * 排序越小越靠前
     */
    private Integer sort;

    private Long hotspotId;

    private Long componentId;

    private String pageName;

    private Long pageId;

    private String version;

    private Long versionId;

    private LinkDto linkDto;

    private LocalDateTime updatedTime;

    /**
     * 热区位置
     */
    private WebJsonBo.HotspotPartsBo style;

    private Integer status;
}

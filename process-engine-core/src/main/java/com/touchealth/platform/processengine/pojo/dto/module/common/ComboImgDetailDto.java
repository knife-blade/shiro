package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComboImgDetailDto extends BaseDto {
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
     * 排序越小越靠前
     */
    private Integer sort;

    private Long comboImgId;

    private Long componentId;

    private String pageName;

    private Long pageId;

    private String version;

    private Long versionId;

    private LinkDto linkDto;

    private LocalDateTime updatedTime;

    private Integer status;
}

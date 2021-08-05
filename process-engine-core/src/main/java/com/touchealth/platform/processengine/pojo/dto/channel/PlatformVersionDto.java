package com.touchealth.platform.processengine.pojo.dto.channel;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liufengqiang
 * @date 2020-11-16 18:07:35
 */
@Data
public class PlatformVersionDto {

    private Long id;
    /** 版本名 */
    private String versionName;
    /** 更新时间 */
    private LocalDateTime updatedTime;
    /** 版本状态 0.草稿 1.是否提交审批 3.已发布 */
    private Integer status;
}

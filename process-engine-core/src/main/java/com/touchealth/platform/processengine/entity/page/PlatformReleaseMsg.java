package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liufengqiang
 * @date 2021-01-25 14:50:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("platform_release_msg")
public class PlatformReleaseMsg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long versionId;
    private String message;
}

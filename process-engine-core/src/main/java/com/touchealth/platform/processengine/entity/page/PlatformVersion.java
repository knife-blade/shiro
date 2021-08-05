package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liufengqiang
 * @date 2020-11-18 14:31:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("platform_version")
public class PlatformVersion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 渠道号 */
    private String channelNo;
    /** 版本名 */
    private String versionName;
    /** 版本状态
     * @see PageCenterConsts.VersionStatus */
    private Integer status;
    /** 提交审核人 */
    private Long authUserId;
}

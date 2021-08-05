package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户权限模板关系表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserPermsTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所在的渠道编码
     */
    private String channelNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postJobId;

    public UserPermsTemplate(String channelNo, Long userId, Long postJobId) {
        this.channelNo = channelNo;
        this.userId = userId;
        this.postJobId = postJobId;
    }
}

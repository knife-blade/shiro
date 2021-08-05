package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 员工和渠道关系表
 * </p>
 *
 * @author SunYang
 * @since 2021-01-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserChannel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    private Long userId;

    /**
     * 渠道编码列表，逗号分隔
     */
    private String channelNoList;


}

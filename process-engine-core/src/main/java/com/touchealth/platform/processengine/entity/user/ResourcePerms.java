package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 资源权限关联表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourcePerms extends BaseEntity {

    /**
     * 资源标识
     */
    private String resource;
    /**
     * 资源类型。
     * {@linkplain com.touchealth.platform.processengine.constant.AuthConstant#RESOURCE_TYPE_PROCESS_ENGINE 0：流程引擎}；
     * {@linkplain com.touchealth.platform.processengine.constant.AuthConstant#RESOURCE_TYPE_PHYSICAL 1：检前}；
     */
    private Integer resourceType;
    /**
     * 权限列表，逗号分割
     */
    private String perms;
    /**
     * 应用名
     */
    private String appName;

}

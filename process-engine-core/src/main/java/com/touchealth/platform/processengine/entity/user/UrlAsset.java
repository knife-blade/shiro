package com.touchealth.platform.processengine.entity.user;

import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * URL资源表
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UrlAsset extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 资源地址
     */
    private String url;

    /**
     * 资源名
     */
    private String name;

    /**
     * 权限资源ID集合，逗号分隔
     */
    private String permIds;


}

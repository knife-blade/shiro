package com.touchealth.platform.processengine.constant;

/**
 * 授权相关常量
 *
 * @author SY
 */
public interface AuthConstant {

    /**
     * 权限白名单
     */
    String[] PERMS_WHITELIST = new String[]{
            "/platform-channel/businessType",
            "/page-manager/preset",
            "/platform-channel/admin-account",
            "/platform-channel",
            "/user/validateCode",
            "/mobile/user/get-wechat-openId"
    };

    /**
     * 资源类型 - 流程引擎
     */
    Integer RESOURCE_TYPE_PROCESS_ENGINE = 0;
    /**
     * 资源类型 - 检前
     */
    Integer RESOURCE_TYPE_PHYSICAL = 1;

}

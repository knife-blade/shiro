package com.touchealth.platform.processengine.pojo.request.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    /**
     * 登录邮箱
     */
    private String email;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 登录地址
     */
    private String loginArea;

    /**
     * 登录类型；0|账户密码登录 1|手机号短信验证码登录
     */
    private Integer loginType = 0;

    /**
     * 登录手机号
     */
    private String mobile;

    /**
     * 短信验证码
     */
    private String verificationCode;

    private String referer;

    private String ipAddress;

}

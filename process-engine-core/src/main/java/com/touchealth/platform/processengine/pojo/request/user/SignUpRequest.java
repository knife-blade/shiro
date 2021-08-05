package com.touchealth.platform.processengine.pojo.request.user;

import lombok.Data;

import java.util.Map;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2021/1/15
 **/
@Data
public class SignUpRequest {

    /**
     * 登录手机号
     */
    private String mobile;

    /**
     * 短信验证码
     */
    private String verificationCode;

    private String ipAddress;

    private String referer;

    private Integer clientType;

    private Map authInfo;

    private String appId;
}

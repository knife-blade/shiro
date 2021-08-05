package com.touchealth.platform.processengine.api.impl;

import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.process.engine.api.ProcessUserApi;
import com.touchealth.process.engine.dto.user.TokenUserInfoDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component("processUserApi")
public class ProcessUserApiImpl implements ProcessUserApi {

    @Resource
    private UserService userService;

    @Override
    public TokenUserInfoDto validToken(String token) {
        Map<String, String> userInfo = userService.validToken(token);
        TokenUserInfoDto dto = new TokenUserInfoDto();
        if (null != userInfo) {
            dto.setValidSuccess(true);
            dto.setUserInfoInToken(userInfo);
        } else {
            dto.setValidSuccess(false);
        }
        return dto;
    }
}

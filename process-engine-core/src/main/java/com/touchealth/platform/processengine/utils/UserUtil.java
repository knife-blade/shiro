package com.touchealth.platform.processengine.utils;

import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.dto.UserLoginInfoDto;
import com.touchealth.platform.processengine.service.user.UserService;
import org.springframework.core.NamedThreadLocal;

import javax.annotation.Resource;

public class UserUtil {

    private static final ThreadLocal<UserLoginInfoDto> USER_LOGIN_INFO_THREAD_LOCAL = new NamedThreadLocal<>("userLoginInfo");

    public static void setUserLoginInfo(UserLoginInfoDto info) {
        USER_LOGIN_INFO_THREAD_LOCAL.set(info);
    }

    public static UserLoginInfoDto getUserLoginInfo() {
        return USER_LOGIN_INFO_THREAD_LOCAL.get();
    }

    public static Long getUserId() {
        if (null == getUserLoginInfo()) {
            return 0L;
        }
        return getUserLoginInfo().getUserId();
    }

    private static UserService userService;

    @Resource
    public void setUserService(UserService userService) {
        UserUtil.userService = userService;
    }

    /**
     * 根据userId获取用户详细信息
     */
    public static User findUser(Long userId) {
        return userService.findById(userId);
    }
}

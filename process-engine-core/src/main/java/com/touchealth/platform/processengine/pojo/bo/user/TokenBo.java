package com.touchealth.platform.processengine.pojo.bo.user;

import com.touchealth.platform.processengine.entity.user.User;
import lombok.Data;

@Data
public class TokenBo {

    private String token;

    private User user;
    /**
     * 用户登录唯一标识
     * 同一个用户两次登录，会有两个不同的的登录标识
     */
    private String currentUserUniqueMark;
}

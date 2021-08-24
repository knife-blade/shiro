package com.example.demo.common.utils;

import com.example.demo.config.shiro.entity.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {

    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}
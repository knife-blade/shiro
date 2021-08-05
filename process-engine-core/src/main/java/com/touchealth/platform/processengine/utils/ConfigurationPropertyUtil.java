package com.touchealth.platform.processengine.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class ConfigurationPropertyUtil {

    private static Environment environment;

    @Resource
    public void setEnvironment(Environment environment) {
        ConfigurationPropertyUtil.environment = environment;
    }

    /**
     * 从Spring容器获取配置信息
     */
    public static String getProperty(String propertyName) {
        return environment.getProperty(propertyName);
    }
}

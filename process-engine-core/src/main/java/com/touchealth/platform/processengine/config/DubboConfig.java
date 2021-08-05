package com.touchealth.platform.processengine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:dubbo/spring-dubbo.xml"})
public class DubboConfig {
}

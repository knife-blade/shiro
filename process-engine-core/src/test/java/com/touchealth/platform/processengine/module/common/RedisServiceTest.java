package com.touchealth.platform.processengine.module.common;

import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.service.common.RedisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisServiceTest extends BaseTest {

    @Autowired
    private RedisService redisService;

    private final static String LOGIN_WEB_JSON_TEMPLATE = "{\"belongType\":0,\"category\":0,\"moduleType\":7,\"isInitModule\":true,\"name\":\"login\",\"blockId\":\"e0c73ed5-65b9-4bb5-9ea2-9fa9f5e016d1\",\"layoutType\":1,\"status\":\"PREVIEW\",\"data\":{\"agreementUrlChinese\":\"\",\"agreementUrlEng\":\"\",\"privacyAgreementUrlChinese\":\"\",\"privacyAgreementUrlEng\":\"\",\"subtitleChinese\":\"一站式健康管理平台\",\"subtitleEng\":\"Health Platform \",\"titleChinese\":\"势成一健康\",\"titleEng\":\"Touchealth\"}}";
    @Test
    public void keyDemo() {

        redisService.setValue(RedisConstant.LOGIN_WEB_JSON_TEMPLATE, LOGIN_WEB_JSON_TEMPLATE);

        String json = redisService.getValue(RedisConstant.LOGIN_WEB_JSON_TEMPLATE);
        System.out.println(json);
    }

}

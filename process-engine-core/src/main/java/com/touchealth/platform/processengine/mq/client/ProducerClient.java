package com.touchealth.platform.processengine.mq.client;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.touchealth.platform.processengine.config.MqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/8
 **/
@Slf4j
@Configuration
public class ProducerClient {

    @Autowired
    private MqConfig mqConfig;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildProducer() {
        //ProducerBean用于将Producer集成至Spring Bean中
        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(mqConfig.getProperties());
        log.info("rocketmq producer init success");
        return producerBean;
    }

}

package com.touchealth.platform.processengine.service.impl.user;

import com.touchealth.platform.processengine.ProcessEngineApplication;
import com.touchealth.platform.processengine.constant.RocketMqMsgTagConstant;
import com.touchealth.platform.processengine.service.ProducerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: process-engine
 * @description: TODO
 * @author: xianghy
 * @create: 2020/12/8
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessEngineApplication.class)
public class ProducerServiceTest {

    @Autowired
    private ProducerService producerService;

    @Test
    public void sendMsg() {

        String message = "rocketMQ测试";
        producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BANNER, message);
    }

}

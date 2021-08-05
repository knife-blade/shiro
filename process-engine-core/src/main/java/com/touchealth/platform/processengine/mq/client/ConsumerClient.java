package com.touchealth.platform.processengine.mq.client;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.touchealth.platform.processengine.config.MqConfig;
import com.touchealth.platform.processengine.mq.listener.RocketMqMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @program: process-engine
 * @description: TODO
 * @author: xianghy
 * @create: 2020/12/8
 **/
@Slf4j
@Configuration
public class ConsumerClient {

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private RocketMqMessageListener rocketMqMessageListener;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean buildConsumer() {
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = mqConfig.getProperties();
        properties.setProperty(PropertyKeyConst.GROUP_ID, mqConfig.getGroupId());

        //将消费者线程数固定为20个 20为默认值
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums, "20");
        consumerBean.setProperties(properties);

        //订阅消息
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>(4);
        //订阅普通消息
        Subscription subscription = new Subscription();
        subscription.setTopic(mqConfig.getTopic());
        subscription.setExpression(mqConfig.getTag());
        subscriptionTable.put(subscription, rocketMqMessageListener);

        consumerBean.setSubscriptionTable(subscriptionTable);
        log.info("rocketmq consumer init success");
        return consumerBean;
    }


}

package com.touchealth.platform.processengine.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/8
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class MqConfig {

    /**
     * 阿里云accessKey
     */
    private String accessKey;

    /**
     * 阿里云secretKey
     */
    private String secretKey;

    /**
     * 协议公网接入地址
     */
    private String nameSrvAddr;

    /**
     * 消息发送失败重试次数
     */
    private String retryTimesSendFailed;

    /**
     * 消息发送超时时间，毫秒
     */
    private String sendMsgTimeoutMillis;

    /**
     * 消息topic
     */
    private String topic;

    /**
     * groupId
     */
    private String groupId;

    /**
     * tag  *表示全部
     */
    private String tag;

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, nameSrvAddr);

        properties.setProperty(PropertyKeyConst.GROUP_ID, groupId);

        // 消息发送失败重试次数
        properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, retryTimesSendFailed);

        // 设置发送超时时间，单位毫秒
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendMsgTimeoutMillis);
        return properties;
    }

}

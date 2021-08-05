package com.touchealth.platform.processengine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.touchealth.platform.processengine.config.MqConfig;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.bo.MyThreadTaskAbortPolicy;
import com.touchealth.platform.processengine.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/8
 **/
@Slf4j
@Service("producerService")
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ProducerBean producerBean;

    /**
     * 线程池
     */
    public static ExecutorService rocketMQPool = initPool("MQNormalProducer");


    /**
     * 初始化线程
     *
     * @param type
     * @return
     */
    private static ExecutorService initPool(String type) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(type + "-MQNormalProducer-pool-%d").build();
        return new ThreadPoolExecutor(4, 8,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1000),
                namedThreadFactory,
                new MyThreadTaskAbortPolicy(log, type));
    }

    @Override
    public void sendMsg(String msgTag, String messageBody) {
        if (StringUtils.isEmpty(messageBody)) {
            throw new BusinessException("消息内容不能为空！");
        }

        Message msg = new Message(mqConfig.getTopic(), msgTag, null, messageBody.getBytes());
        SendResult sendResult = producerBean.send(msg);
        //获取发送结果，不抛异常即发送成功
        if (sendResult != null) {
            success(msg, sendResult.getMessageId());
            return;
        }
        error(msg, new Exception("消息发送失败！"));
    }

    @Override
    public void sendMsg(String msgTag, Object object) {
        if (null ==  object) {
            throw new BusinessException("消息内容不能为空！");
        }
        sendMsg(msgTag, JSONObject.toJSONString(object));
    }

    @Override
    public void sendAsyncMsg(String msgTag, String messageBody) {
        if (StringUtils.isEmpty(messageBody)) {
            throw new BusinessException("消息内容不能为空！");
        }

        producerBean.setCallbackExecutor(rocketMQPool);
        Message msg = new Message(mqConfig.getTopic(), msgTag, null, messageBody.getBytes());
        try {
            producerBean.sendAsync(msg, new SendCallback() {
                @Override
                public void onSuccess(final SendResult sendResult) {
                    assert sendResult != null;
                    success(msg, sendResult.getMessageId());
                }
                @Override
                public void onException(final OnExceptionContext context) {
                    // 打印
                    error(msg, context.getException());

                    // 发送失败，直接使用同步发送
                    sendMsg(msgTag, messageBody);
                }
            });
        } catch (ONSClientException e) {
            error(msg,e);
        }
    }

    @Override
    public void sendAsyncMsg(String msgTag, Object object) {
        sendAsyncMsg(msgTag, JSONObject.toJSONString(object));
    }

    private void error(Message msg,Exception e) {
        log.error("发送MQ消息失败-- Topic:{}, Key:{}, tag:{}, body:{}", msg.getTopic(), msg.getKey(), msg.getTag(), new String(msg.getBody()));
        log.error("errorMsg --- {}", e.getMessage());
    }

    private void success(Message msg,String messageId) {
        log.info("发送MQ消息成功 -- Topic:{} ,msgId:{} , Key:{}, tag:{}, body:{}", msg.getTopic(), messageId, msg.getKey(), msg.getTag(), new String(msg.getBody()));
    }

}

package com.touchealth.platform.processengine.service;

/**
 * @program: process-engine
 * @description: 消息中间件接口
 * @author: xianghy
 * @create: 2020/12/8
 **/
public interface ProducerService {

    /**
     * 发送消息
     * @param msgTag 消息tag
     * @param messageBody 消息内容
     */
    void sendMsg(String msgTag, String messageBody);

    /**
     * 发送消息
     * @param msgTag 消息tag
     * @param object 消息内容
     */
    void sendMsg(String msgTag, Object object);

    /**
     * 发送异步消息
     * @param msgTag 消息tag
     * @param messageBody 消息内容
     */
    void sendAsyncMsg(String msgTag, String messageBody);

    /**
     * 发送异步消息
     * @param msgTag 消息tag
     * @param object 消息内容
     */
    void sendAsyncMsg(String msgTag, Object object);
}

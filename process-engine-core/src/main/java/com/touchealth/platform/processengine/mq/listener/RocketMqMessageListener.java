package com.touchealth.platform.processengine.mq.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.touchealth.platform.processengine.constant.RocketMqMsgTagConstant;
import com.touchealth.platform.processengine.pojo.request.datastatistics.MqRequest;
import com.touchealth.platform.processengine.service.datastatistics.DataStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: process-engine
 * @description: rocketmq普通消息消费者
 * @author: xianghy
 * @create: 2020/12/8
 **/
@Slf4j
@Component
public class RocketMqMessageListener implements MessageListener {


    @Autowired
    private DataStatisticsService dataStatisticsService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        String msgTag = message.getTag();
        String msgBody = new String(message.getBody());
        log.info("接收到MQ消息. Topic={},tag={},body={}", message.getTopic(), msgTag, new String(message.getBody()));
        try {
            MqRequest mqRequest = JSONObject.parseObject(msgBody, MqRequest.class);
            // 根据tag区分消息
            switch (msgTag) {
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BANNER:
                    // banner组件处理
                    dataStatisticsService.processDataStatisticsForBannerRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_NAVIGATE:
                    // navigate组件处理
                    dataStatisticsService.processDataStatisticsForNavigateRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_COMBO_IMG:
                    // comboImg组件处理
                    dataStatisticsService.processDataStatisticsForComboImgRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_HOTSPOT:
                    // hotspot组件处理
                    dataStatisticsService.processDataStatisticsForHotspotRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BUTTON:
                    // button组件处理
                    dataStatisticsService.processDataStatisticsForButtonRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_LOGIN:
                    // button组件处理
                    dataStatisticsService.processDataStatisticsForLoginRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_PERSONAL_INFO:
                    // personal组件处理
                    dataStatisticsService.processDataStatisticsForPersonalInfoRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_ORDER_MANAGEMENT:
                    // orderManagement组件处理
                    dataStatisticsService.processDataStatisticsForOrderManagementRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_MY_MOD:
                    // myMod组件处理
                    dataStatisticsService.processDataStatisticsForMyModRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_HOME_NAV:
                    // homeNav组件处理
                    dataStatisticsService.processDataStatisticsForHomeNavRequest(mqRequest);
                    break;
                case RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BLANK:
                    // blank组件处理
                    dataStatisticsService.processDataStatisticsForBlankRequest(mqRequest);
                    break;
                default:
                    log.error("rocketmq 消费失败，tag={}, message={}", msgTag, message);
            }
            // 消费成功
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error("消费MQ消息失败！ msgId:" + message.getMsgID() + "----ExceptionMsg:"+e.getMessage());
            return Action.ReconsumeLater;
        }
    }
}

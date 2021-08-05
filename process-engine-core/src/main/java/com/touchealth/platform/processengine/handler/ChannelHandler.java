package com.touchealth.platform.processengine.handler;

import org.apache.commons.lang3.StringUtils;

/**
 * 用户和渠道绑定关系处理类
 */
public class ChannelHandler {

    private static final ThreadLocal<String> CURRENT_CHANNEL = new ThreadLocal<>();

    public static String getChannelNo() {
        if (CURRENT_CHANNEL.get() == null) {
            return "";
        }
        return CURRENT_CHANNEL.get();
    }

    public static void setChannelNo(String channelNo) {
        CURRENT_CHANNEL.remove();
        if (StringUtils.isNotEmpty(channelNo)) {
            CURRENT_CHANNEL.set(channelNo);
        }
    }

}

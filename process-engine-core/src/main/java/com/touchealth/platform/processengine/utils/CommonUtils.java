package com.touchealth.platform.processengine.utils;

import java.util.UUID;

/**
 * 通用工具类
 *
 * @author liufengqiang
 * @date 2020-12-22 14:24:32
 */
public class CommonUtils {

    /**
     * HashMap初始容量
     *
     * @param size
     * @return
     */
    public static int getInitialCapacity(int size) {
        return size * 4 / 3 + 1;
    }

    /**
     * UUID
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleClickTimeLog;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface ModuleClickTimeLogService {

    /**
     * 新增
     * @param timeLog
     */
    void save(ModuleClickTimeLog timeLog);

    /**
     * 根据渠道code统计对应的点击次数
     * @param channelCode 渠道号码
     * @return
     */
    long countByChannelNo(String channelCode);
}

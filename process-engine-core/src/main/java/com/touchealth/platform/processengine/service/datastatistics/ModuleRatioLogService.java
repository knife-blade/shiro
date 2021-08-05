package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleRatioLog;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface ModuleRatioLogService {

    /**
     * 新增
     * @param ratioLog
     */
    void save(ModuleRatioLog ratioLog);
}

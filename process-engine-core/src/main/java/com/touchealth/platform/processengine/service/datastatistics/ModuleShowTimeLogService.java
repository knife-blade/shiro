package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleShowTimeLog;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface ModuleShowTimeLogService {

    /**
     * 新增
     * @param timeLog
     */
    void save(ModuleShowTimeLog timeLog);
}

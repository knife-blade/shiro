package com.touchealth.platform.processengine.service.impl.datastatistics;

import com.touchealth.platform.processengine.dao.datastatistics.ModuleRatioLogDao;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleRatioLog;
import com.touchealth.platform.processengine.service.datastatistics.ModuleRatioLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: process-engine
 * @description: TODO
 * @author: xianghy
 * @create: 2020/12/2
 **/
@Service("moduleRatioLogService")
public class ModuleRatioLogServiceImpl implements ModuleRatioLogService {

    @Resource
    private ModuleRatioLogDao moduleRatioLogDao;

    @Override
    public void save(ModuleRatioLog ratioLog) {
        moduleRatioLogDao.save(ratioLog);
    }
}

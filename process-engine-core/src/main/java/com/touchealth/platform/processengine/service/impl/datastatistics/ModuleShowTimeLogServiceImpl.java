package com.touchealth.platform.processengine.service.impl.datastatistics;

import com.touchealth.platform.processengine.dao.datastatistics.ModuleShowTimeLogDao;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleShowTimeLog;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.service.datastatistics.ModuleShowTimeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/2
 **/
@Service("moduleShowTimeLogService")
public class ModuleShowTimeLogServiceImpl implements ModuleShowTimeLogService {

    @Autowired
    private ModuleShowTimeLogDao moduleShowTimeLogDao;

    @Override
    public void save(ModuleShowTimeLog timeLog) {
        if (timeLog == null) {
            throw new BusinessException("参数不能为空！");
        }

        if (timeLog.getCreateTime() == null) {
            timeLog.setCreateTime(new Date());
        }

        moduleShowTimeLogDao.save(timeLog);
    }
}

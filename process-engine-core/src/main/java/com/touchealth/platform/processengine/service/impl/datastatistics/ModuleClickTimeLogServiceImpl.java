package com.touchealth.platform.processengine.service.impl.datastatistics;

import cn.hutool.core.lang.Assert;
import com.touchealth.platform.processengine.dao.datastatistics.ModuleClickTimeLogDao;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleClickTimeLog;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.service.datastatistics.ModuleClickTimeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/2
 **/
@Service("moduleClickTimeLogService")
public class ModuleClickTimeLogServiceImpl implements ModuleClickTimeLogService {

    @Autowired
    private ModuleClickTimeLogDao moduleClickTimeLogDao;

    @Override
    public void save(ModuleClickTimeLog timeLog) {
        if (timeLog == null) {
            throw new BusinessException("参数不能为空！");
        }

        if (timeLog.getCreateTime() == null) {
            timeLog.setCreateTime(new Date());
        }

        moduleClickTimeLogDao.save(timeLog);
    }

    @Override
    public long countByChannelNo(String channelCode) {
        Assert.notBlank(channelCode, "参数不能为空！");
        return moduleClickTimeLogDao.countByChannelNo(channelCode);
    }
}

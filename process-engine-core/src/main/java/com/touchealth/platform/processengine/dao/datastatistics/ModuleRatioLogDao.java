package com.touchealth.platform.processengine.dao.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleRatioLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Component
public class ModuleRatioLogDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(ModuleRatioLog ratioLog) {
        mongoTemplate.save(ratioLog);
    }
}

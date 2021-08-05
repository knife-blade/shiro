package com.touchealth.platform.processengine.dao.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleShowTimeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Component
public class ModuleShowTimeLogDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(ModuleShowTimeLog timeLog) {
        mongoTemplate.save(timeLog);
    }
}

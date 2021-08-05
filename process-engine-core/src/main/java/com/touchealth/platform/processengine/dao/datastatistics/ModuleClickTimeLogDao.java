package com.touchealth.platform.processengine.dao.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.ModuleClickTimeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Component
public class ModuleClickTimeLogDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(ModuleClickTimeLog timeLog) {
        mongoTemplate.save(timeLog);
    }

    public long countByChannelNo(String channelCode) {
        Query query = new Query(Criteria.where("channelNo").is(channelCode));
        return mongoTemplate.count(query, ModuleClickTimeLog.class);
    }
}

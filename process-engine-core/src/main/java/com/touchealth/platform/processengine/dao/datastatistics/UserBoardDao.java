package com.touchealth.platform.processengine.dao.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.UserBoard;
import com.touchealth.platform.processengine.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
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
public class UserBoardDao  {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(UserBoard userBoard) {
        mongoTemplate.save(userBoard);
    }

    public long countByUniqueId(String uniqueId) {
        if (StringUtils.isEmpty(uniqueId)) {
            throw new BusinessException("参数不能为空！");
        }
        Query query = new Query(Criteria.where("uniqueId").is(uniqueId));
        return mongoTemplate.count(query, UserBoard.class);
    }

}

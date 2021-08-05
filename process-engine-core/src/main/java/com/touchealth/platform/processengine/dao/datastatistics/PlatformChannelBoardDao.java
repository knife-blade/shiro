package com.touchealth.platform.processengine.dao.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.PlatformChannelBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Component
public class PlatformChannelBoardDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(PlatformChannelBoard channelBoard) {
        mongoTemplate.save(channelBoard);
    }

    public void save(List<PlatformChannelBoard> channelBoardList) {
        mongoTemplate.insert(channelBoardList, PlatformChannelBoard.class);
    }

}

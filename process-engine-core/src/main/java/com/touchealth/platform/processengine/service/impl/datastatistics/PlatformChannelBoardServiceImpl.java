package com.touchealth.platform.processengine.service.impl.datastatistics;

import com.touchealth.platform.processengine.dao.datastatistics.PlatformChannelBoardDao;
import com.touchealth.platform.processengine.dao.page.PlatformChannelDao;
import com.touchealth.platform.processengine.entity.datastatistics.PlatformChannelBoard;
import com.touchealth.platform.processengine.service.datastatistics.PlatformChannelBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/24
 **/
@Service("channelBoardService")
public class PlatformChannelBoardServiceImpl implements PlatformChannelBoardService {

    @Autowired
    private PlatformChannelBoardDao platformChannelBoardDao;

    @Override
    public void save(List<PlatformChannelBoard> channelBoardList) {
        Assert.notEmpty(channelBoardList, "参数不能为空！");
        platformChannelBoardDao.save(channelBoardList);
    }
}

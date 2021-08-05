package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.PlatformChannelBoard;

import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface PlatformChannelBoardService {

    /**
     * 批量新增
     * @param channelBoardList
     */
    void save(List<PlatformChannelBoard> channelBoardList);
}

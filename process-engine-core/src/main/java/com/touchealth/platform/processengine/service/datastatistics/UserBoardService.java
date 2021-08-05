package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.entity.datastatistics.UserBoard;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface UserBoardService {

    /**
     * 新增
     * @param userBoard
     */
    void save(UserBoard userBoard);

    /**
     * 获取可用的唯一ID
     * @return UUID
     */
    String getUniqueId();
}

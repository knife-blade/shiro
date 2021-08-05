package com.touchealth.platform.processengine.service.impl.datastatistics;

import com.touchealth.platform.processengine.dao.datastatistics.UserBoardDao;
import com.touchealth.platform.processengine.entity.datastatistics.UserBoard;
import com.touchealth.platform.processengine.service.datastatistics.UserBoardService;
import com.touchealth.platform.processengine.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/24
 **/
@Service("userBoardService")
public class UserBoardServiceImpl implements UserBoardService {

    @Autowired
    private UserBoardDao userBoardDao;

    @Override
    public void save(@Validated @NotNull(message = "参数不能为空") UserBoard userBoard) {
        userBoardDao.save(userBoard);
    }

    @Override
    public String getUniqueId() {
        String userBoardUniqueId;
        do {
            userBoardUniqueId = CommonUtils.uuid();
        } while (isUniqueId(userBoardUniqueId));

        return userBoardUniqueId;
    }

    private boolean isUniqueId(String uniqueId) {
       return userBoardDao.countByUniqueId(uniqueId) > 0;
    }
}

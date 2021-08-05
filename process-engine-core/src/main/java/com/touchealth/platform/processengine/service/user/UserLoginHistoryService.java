package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Date;
import java.util.List;

public interface UserLoginHistoryService extends BaseService<UserLoginHistory> {

    /**
     * 根据用户ID和登录UUID获取登录信息
     * @param userId 用户ID
     * @param currentUserRemark
     * @param userType
     * @return
     */
    UserLoginHistory getByUserIdAndSignCode(Long userId, String currentUserRemark, Integer userType);

    /**
     * 根据ID更新登录记录过期时间
     * @param id ID
     * @param expirationTime
     * @return
     */
    int updateExpirationTimeById(Long id, Date expirationTime);

    UserLoginHistory getByUserIdAndUserType(Long userId, Integer userType);
}

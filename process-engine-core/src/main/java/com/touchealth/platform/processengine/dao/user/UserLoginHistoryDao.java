package com.touchealth.platform.processengine.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserLoginHistoryDao extends BaseMapper<UserLoginHistory> {

    /**
     * 更新过期时间
     * @param id ID
     * @param expirationTime 过期时间
     * @return
     */
    int updateExpirationTimeById(@Param("id") Long id, @Param("expirationTime") Date expirationTime);
}

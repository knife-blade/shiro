package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.UserPerms;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 用户权限关系表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface UserPermsService extends BaseService<UserPerms> {

    /**
     * 查询指定权限code对应的用户
     * @param permsCode
     * @param channelNo
     * @return
     */
    List<UserPerms> listByPermsCode(String permsCode, String channelNo);
}

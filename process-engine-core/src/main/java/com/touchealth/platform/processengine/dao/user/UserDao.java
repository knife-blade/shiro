package com.touchealth.platform.processengine.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.bo.user.PostJobUserCountBo;

import java.util.List;

public interface UserDao extends BaseMapper<User> {

    /**
     * 查询岗位人数
     * @param postJobIdList
     * @return
     */
    List<PostJobUserCountBo> countByPostJobIds(List<Long> postJobIdList);

}

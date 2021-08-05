package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.user.UserLoginHistoryDao;
import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.UserLoginHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service("userLoginHistoryService")
public class UserLoginHistoryServiceImpl
        extends BaseServiceImpl<UserLoginHistoryDao, UserLoginHistory>
        implements UserLoginHistoryService {

    @Resource
    private UserLoginHistoryDao userLoginHistoryDao;


    @Override
    public UserLoginHistory getByUserIdAndSignCode(Long userId, String currentUserRemark, Integer userType) {
        Assert.isTrue(StringUtils.isNotBlank(currentUserRemark) && null != userId, "参数不能为空！");
        LambdaQueryWrapper<UserLoginHistory> queryWrapper = Wrappers.lambdaQuery(UserLoginHistory.class)
                .eq(UserLoginHistory::getUserId, userId)
                .eq(UserLoginHistory::getUserType, userType)
                .eq(UserLoginHistory::getIsDeleted, CommonConstant.IS_NOT_DELETE)
                .eq(UserLoginHistory::getCurrentUserRemark, currentUserRemark);
        List<UserLoginHistory> selectList = userLoginHistoryDao.selectList(queryWrapper);
        return CollectionUtils.isEmpty(selectList) ? null : selectList.get(0);
    }

    @Override
    public int updateExpirationTimeById(Long id, Date expirationTime) {
        return userLoginHistoryDao.updateExpirationTimeById(id, expirationTime);
    }

    @Override
    public UserLoginHistory getByUserIdAndUserType(Long userId, Integer userType) {
        LambdaQueryWrapper<UserLoginHistory> queryWrapper = Wrappers.lambdaQuery(UserLoginHistory.class)
                .eq(UserLoginHistory::getUserId, userId)
                .eq(UserLoginHistory::getUserType, userType)
                .eq(UserLoginHistory::getIsDeleted, CommonConstant.IS_NOT_DELETE)
                .orderByDesc(UserLoginHistory::getLoginTime);
        List<UserLoginHistory> selectList = userLoginHistoryDao.selectList(queryWrapper);
        return CollectionUtils.isEmpty(selectList) ? null : selectList.get(0);
    }
}

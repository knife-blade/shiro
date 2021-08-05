package com.touchealth.platform.processengine.handler;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import com.touchealth.platform.processengine.pojo.bo.user.TokenBo;
import com.touchealth.platform.processengine.utils.DateUtil;
import com.touchealth.platform.processengine.utils.IpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class UserLoginHistoryHandler {

    public static UserLoginHistory instance(TokenBo tokenBo, String referer, String idAddress, Integer userType) {
        UserLoginHistory history = new UserLoginHistory();
        history.setId(IdWorker.getId(UserLoginHistory.class));
        history.setUserType(userType);
        history.setUserId(tokenBo.getUser().getId());
        history.setCurrentUserRemark(tokenBo.getCurrentUserUniqueMark());
        history.setReferer(referer);
        history.setLoginStatus(1);
        history.setIpAddress(idAddress);
        history.setIsDisable(0);
        history.setIsDeleted(0L);
        history.setVersion(0);
        history.setLoginTime(new Date());
        history.setExpirationTime(DateUtil.add(new Date(), 7));
        return history;
    }
}

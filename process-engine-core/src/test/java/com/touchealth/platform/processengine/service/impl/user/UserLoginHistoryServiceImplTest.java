package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchealth.platform.processengine.ProcessEngineApplication;
import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import com.touchealth.platform.processengine.service.user.UserLoginHistoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessEngineApplication.class)
public class UserLoginHistoryServiceImplTest {

    @Resource
    private UserLoginHistoryService loginHistoryService;

    @Test
    public void save() {
        UserLoginHistory history = new UserLoginHistory();
        history.setId(IdWorker.getId(UserLoginHistory.class));
        history.setCurrentUserRemark("currentUserRemark");
        history.setIpAddress("192.168.137.69");
        history.setIsDeleted(0L);
        history.setIsDisable(0);
        history.setLoginStatus(1);
        history.setLoginArea("杭州市");
        history.setLoginTime(new Date());
        history.setReferer("referer");
        history.setUserId(12L);
        history.setUserType(1);
        history.setVersion(0);
        loginHistoryService.save(history);
        UserLoginHistory historyInner = loginHistoryService.getById(history.getId());
        historyInner.setLogoutTime(new Date());
        historyInner.setLoginStatus(2);
        loginHistoryService.updateById(historyInner);

    }

    @Test
    public void test2() {
        UserLoginHistory historyInner = loginHistoryService.getById(1336575399396139010L);
        historyInner.setLogoutTime(new Date());
        historyInner.setLoginStatus(2);
        loginHistoryService.updateById(historyInner);
        historyInner = loginHistoryService.getById(1336575399396139010L);
        historyInner.setLogoutTime(new Date());
        historyInner.setLoginStatus(2);
        loginHistoryService.updateById(historyInner);
        historyInner = loginHistoryService.getById(1336575399396139010L);
        historyInner.setLogoutTime(new Date());
        historyInner.setLoginStatus(2);
        loginHistoryService.updateById(historyInner);
    }
}
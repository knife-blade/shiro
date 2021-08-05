package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.ProcessEngineApplication;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.bo.user.PostJobUserCountBo;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.UserDto;
import com.touchealth.platform.processengine.pojo.request.user.UserSearchRequest;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessEngineApplication.class)
public class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Test
    public void findUser() {
        QueryWrapper<User> query = Wrappers.query();
        query.eq("email", "123@163.com");
        query.eq("user_type", 1);
        query.eq("is_disable", 0);
        query.eq("is_del", 0);
        User user = userService.getOne(query);
        System.out.println(JsonUtil.getJsonFromObject(user));
    }

    @Test
    public void pageListTest() {
        UserSearchRequest req = new UserSearchRequest();
        req.setChannelNo("CN59964");
        req.setSearch("111");
        PageData<UserDto> res = userService.pageList(req);
        Assert.assertNotNull(res);
    }

    @Test
    public void countByPostJobIdsTest() {
        List<PostJobUserCountBo> res = userService.countByPostJobIds(Arrays.asList(1346303114276356097L));
        Assert.assertNotNull(res);
    }

}
package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.dao.user.UserPermsDao;
import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.entity.user.UserPerms;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.PermsService;
import com.touchealth.platform.processengine.service.user.UserPermsService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户权限关系表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Service
public class UserPermsServiceImpl extends BaseServiceImpl<UserPermsDao, UserPerms> implements UserPermsService {

    @Resource
    private PermsService permsService;

    @Override
    public List<UserPerms> listByPermsCode(String permsCode, String channelNo) {
        Perms perms = permsService.getByPermsCode(permsCode);
        Assert.notNull(perms, "权限配置异常");
        return list(Wrappers.<UserPerms>lambdaQuery().eq(UserPerms::getPermsId, perms.getId()).eq(UserPerms::getChannelNo, channelNo));
    }
}

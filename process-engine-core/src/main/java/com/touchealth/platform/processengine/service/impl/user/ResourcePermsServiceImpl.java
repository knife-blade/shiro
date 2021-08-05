package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.basic.constant.CommonConstant;
import com.touchealth.platform.basic.constant.RedisKeyConstant;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.dao.user.ResourcePermsDao;
import com.touchealth.platform.processengine.entity.user.ResourcePerms;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.ResourcePermsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 资源权限关联表 服务实现
 * @author SY
 */
@Service
@Slf4j
public class ResourcePermsServiceImpl extends BaseServiceImpl<ResourcePermsDao, ResourcePerms> implements ResourcePermsService {

    @Resource
    private ResourcePermsDao resourcePermsDao;

    @Cacheable(value = RedisConstant.RESOURCE_PERMS_LIST, key = "\"_\" + #appName")
    @Override
    public List<ResourcePerms> listAll(String appName) {
        return list(Wrappers.<ResourcePerms>lambdaQuery()
                .eq(ResourcePerms::getAppName, appName)
                .eq(ResourcePerms::getDeletedFlag, CommonConstant.DELETED_FLAG_NORMAL));
    }

    @CacheEvict(value = RedisConstant.RESOURCE_PERMS_LIST, key = "\"_\" + #resourcePerms.appName")
    @TransactionalForException
    @Override
    public boolean saveWithTrans(ResourcePerms resourcePerms) {
        return save(resourcePerms);
    }

    @CacheEvict(value = RedisConstant.RESOURCE_PERMS_LIST, key = "\"_\" + #resourcePerms.appName")
    @TransactionalForException
    @Override
    public boolean updateWithTrans(ResourcePerms resourcePerms) {
        Assert.notNull(resourcePerms.getId(), "资源权限ID不能为空");
        return updateById(resourcePerms);
    }

    @CacheEvict(value = RedisConstant.RESOURCE_PERMS_LIST, key = "\"_\" + #resourcePerms.appName")
    @TransactionalForException
    @Override
    public Integer deleteWithTrans(String appName, Long id, Long opUserId) {
        return resourcePermsDao.update(new ResourcePerms(), Wrappers.<ResourcePerms>lambdaUpdate()
                .eq(ResourcePerms::getId, id)
                .eq(ResourcePerms::getAppName, appName)
                .set(ResourcePerms::getDeletedFlag, id)
                .set(ResourcePerms::getUpdatedBy, opUserId)
                .set(ResourcePerms::getUpdatedTime, new Date())
        );
    }

}

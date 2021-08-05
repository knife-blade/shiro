package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.ResourcePerms;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 资源权限关联表 服务类
 * </p>
 *
 * @author SunYang
 */
public interface ResourcePermsService extends BaseService<ResourcePerms> {

    /**
     * 获取指定应用的所有资源和权限
     * @param appName 应用名
     * @return 资源权限信息列表
     */
    List<ResourcePerms> listAll(String appName);

    /**
     * 在事物中添加资源权限
     * @param resourcePerms 资源权限信息
     * @return 是否成功
     */
    boolean saveWithTrans(ResourcePerms resourcePerms);

    /**
     * 在事物中更新资源权限
     * @param resourcePerms 资源权限信息
     * @return 是否成功
     */
    boolean updateWithTrans(ResourcePerms resourcePerms);

    /**
     * 在事物中删除资源权限
     * @param appName 应用名
     * @param id 资源权限ID
     * @param opUserId 操作人ID
     * @return 是否成功
     */
    Integer deleteWithTrans(String appName, Long id, Long opUserId);

}

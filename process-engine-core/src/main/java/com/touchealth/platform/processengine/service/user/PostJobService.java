package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.PostJob;
import com.touchealth.platform.processengine.pojo.request.user.PostJobOpRequest;
import com.touchealth.platform.processengine.service.BaseService;

import java.io.Serializable;

/**
 * <p>
 * 员工岗位表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface PostJobService extends BaseService<PostJob> {

    PostJob updateAndCache(PostJobOpRequest req);

    PostJob saveAndCache(PostJobOpRequest req);

    Boolean deleteAndCache(Long id);

    /**
     * 从缓存中获取数据，若缓存中不存在，则查询后放入缓存
     * @param id
     * @return
     */
    PostJob getCacheById(Serializable id);
}

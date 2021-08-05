package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.dao.user.PostJobDao;
import com.touchealth.platform.processengine.entity.user.PostJob;
import com.touchealth.platform.processengine.pojo.request.user.PostJobOpRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.PostJobService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * <p>
 * 员工岗位表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Service
public class PostJobServiceImpl extends BaseServiceImpl<PostJobDao, PostJob> implements PostJobService {

    @CachePut(value = RedisConstant.REDIS_CACHE_POST_JOB, key = "\"_id_\" + #req.id", unless="#result == null")
    @Override
    public PostJob updateAndCache(PostJobOpRequest req) {
        Long id = req.getId();
        String name = req.getName();

        PostJob postJob = getById(id);
        if (postJob == null) {
            return null;
        }

        boolean updateFlag = update(new PostJob(),
                Wrappers.<PostJob>lambdaUpdate()
                        .eq(PostJob::getId, id)
                        .set(PostJob::getName, name));
        if (updateFlag) {
            postJob.setName(name);
        }
        return postJob;
    }

    @CachePut(value = RedisConstant.REDIS_CACHE_POST_JOB, key = "\"_id_\" + #result.id")
    @Override
    public PostJob saveAndCache(PostJobOpRequest req) {
        Long deptId = req.getDeptId();
        String deptName = req.getName();
        String channelNo = req.getChannelNo();
        PostJob postJob = new PostJob(channelNo, "", deptName, deptId);
        boolean saveFlag = save(postJob);
        if (saveFlag) {
            return postJob;
        }
        return null;
    }

    @CacheEvict(value = RedisConstant.REDIS_CACHE_POST_JOB, key="\"_id_\" + #id")
    @Override
    public Boolean deleteAndCache(Long id) {
        return update(new PostJob(),
                Wrappers.<PostJob>lambdaUpdate()
                        .eq(PostJob::getId, id)
                        .set(PostJob::getDeletedFlag, CommonConstant.IS_DELETE));
    }

    @Cacheable(value = RedisConstant.REDIS_CACHE_POST_JOB, key = "\"_id_\" + #id", unless = "#result == null")
    @Override
    public PostJob getCacheById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

}

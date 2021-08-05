package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.PostJob;
import com.touchealth.platform.processengine.entity.user.PostJobPerms;
import com.touchealth.platform.processengine.pojo.dto.user.PermTemplateDepartmentDto;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 岗位权限关系（权限模板）表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface PostJobPermsService extends BaseService<PostJobPerms> {

    /**
     * 添加或更新岗位权限
     * @param postJob
     * @param permIds
     * @return
     */
    Boolean saveOrUpdateByPostJobId(PostJob postJob, List<Long> permIds);

    /**
     * 根据搜索关键字查询部门岗位权限树
     * @param channelNo
     * @param search
     * @return
     */
    List<PermTemplateDepartmentDto> getDepartmentPermTreeBySearch(String channelNo, String search);

}

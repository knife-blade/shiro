package com.touchealth.platform.processengine.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.entity.user.PostJobPerms;
import com.touchealth.platform.processengine.pojo.dto.user.PermTemplatePostJobDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 岗位权限关系（权限模板）表 Mapper 接口
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface PostJobPermsDao extends BaseMapper<PostJobPerms> {

    /**
     * 根据关键字查询岗位权限列表
     * @param channelNo
     * @param search
     * @return
     */
    List<PermTemplatePostJobDto> getAllBySearch(@Param("channelNo") String channelNo,
                                                @Param("search") String search);

}

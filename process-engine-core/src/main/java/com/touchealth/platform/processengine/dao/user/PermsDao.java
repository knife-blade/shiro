package com.touchealth.platform.processengine.dao.user;

import com.touchealth.platform.processengine.entity.user.Perms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.dto.user.PermDetailDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface PermsDao extends BaseMapper<Perms> {

    /**
     * 查询权限
     * @param channelNo
     * @param type
     * @return
     */
    List<PermDetailDto> listPerms(@Param("channelNo") String channelNo,
                                  @Param("type") Integer type);

    /**
     * 查询用户权限菜单下的权限明细
     * @param channelNo
     * @param menuId
     * @param userId
     * @return
     */
    List<PermDto> listUserPermDetailByMenu(@Param("channelNo") String channelNo,
                                           @Param("menuId") Long menuId,
                                           @Param("userId") Long userId);

    /**
     * 查询岗位权限菜单下的权限明细
     * @param channelNo
     * @param menuId
     * @param postJobId
     * @return
     */
    List<PermDto> listPostJobPermDetailByMenu(@Param("channelNo") String channelNo,
                                              @Param("menuId") Long menuId,
                                              @Param("postJobId") Long postJobId);

    /**
     * 查询用户的所有权限
     * @param channelNo
     * @param userId
     * @return
     */
    List<PermDto> listUserAllPerms(@Param("channelNo") String channelNo,
                                   @Param("userId") Long userId);

    /**
     * 查询所有权限，包括删除的
     * @return
     */
    List<Perms> getAll();

}

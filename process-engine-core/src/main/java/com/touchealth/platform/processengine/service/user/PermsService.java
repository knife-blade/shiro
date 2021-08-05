package com.touchealth.platform.processengine.service.user;

import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.pojo.dto.user.PermDetailDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
public interface PermsService extends BaseService<Perms> {

    /**
     * 查询用户权限菜单（树）
     * @param channelNo
     * @param userId
     * @param isAdmin
     * @return
     */
    List<PermDetailDto> listUserMenu(String channelNo, Long userId, boolean isAdmin);

    /**
     * 查询用户权限菜单（平铺）
     * @param channelNo
     * @param userId
     * @return
     */
    List<PermDetailDto> flatListUserMenu(String channelNo, Long userId);

    /**
     * 查询岗位模板权限菜单（树）
     * @param channelNo
     * @param postJobId
     * @param isAdmin
     * @return
     */
    List<PermDetailDto> listPostJobTempMenu(String channelNo, Long postJobId, boolean isAdmin);

    /**
     * 查询岗位模板权限菜单（平铺）
     * @param channelNo
     * @param postJobId
     * @return
     */
    List<PermDetailDto> flatListPostJobTempMenu(String channelNo, Long postJobId);

    /**
     * 查询用户权限菜单下的权限明细
     * @param channelNo
     * @param menuId
     * @param userId
     * @return
     */
    List<PermDto> listUserPermDetailByMenu(String channelNo, Long menuId, Long userId);

    /**
     * 查询岗位权限菜单下的权限明细
     * @param channelNo
     * @param menuId
     * @param postJobId
     * @return
     */
    List<PermDto> listPostJobPermDetailByMenu(String channelNo, Long menuId, Long postJobId);

    /**
     * 查询用户的所有权限
     * @param channelNo
     * @param userId
     * @return
     */
    List<PermDto> listUserAllPerms(String channelNo, Long userId);

    /**
     * 查询全部权限，包括删除的
     * @return
     */
    List<Perms> getAll();

    /**
     * 根据permsCode查询数据
     * @param permsCode
     * @return
     */
    Perms getByPermsCode(String permsCode);
}

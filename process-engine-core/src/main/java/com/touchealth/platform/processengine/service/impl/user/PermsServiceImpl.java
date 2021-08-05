package com.touchealth.platform.processengine.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.dao.user.PermsDao;
import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.entity.user.PostJobPerms;
import com.touchealth.platform.processengine.entity.user.UserPerms;
import com.touchealth.platform.processengine.pojo.dto.user.PermDetailDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.user.PermsService;
import com.touchealth.platform.processengine.service.user.PostJobPermsService;
import com.touchealth.platform.processengine.service.user.UserPermsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@Service
@Slf4j
public class PermsServiceImpl extends BaseServiceImpl<PermsDao, Perms> implements PermsService {

    @Autowired
    private PermsDao permsDao;
    @Autowired
    private UserPermsService userPermsService;
    @Autowired
    private PostJobPermsService postJobPermsService;

    @Override
    public List<PermDetailDto> listUserMenu(String channelNo, Long userId, boolean isAdmin) {
        Map<Long, Boolean> userHasPermMap = new HashMap<>();

        // 查询用户拥有的权限
        List<UserPerms> userPermsList = userPermsService.list(Wrappers.<UserPerms>lambdaQuery()
                .eq(UserPerms::getUserId, userId)
                .eq(UserPerms::getChannelNo, channelNo)
                .eq(UserPerms::getPermsType, UserConstant.PERM_TYPE_MENU));
        if (CollectionUtils.isNotEmpty(userPermsList)) {
            userHasPermMap = userPermsList.stream().collect(Collectors.toMap(UserPerms::getPermsId, o -> o != null, (ov, nv) -> ov && nv));
        }

        return getPermsMenuTree(channelNo, userHasPermMap, isAdmin);
    }

    @Override
    public List<PermDetailDto> flatListUserMenu(String channelNo, Long userId) {
        Map<Long, Boolean> userHasPermMap = new HashMap<>();

        // 查询用户拥有的权限
        List<UserPerms> userPermsList = userPermsService.list(Wrappers.<UserPerms>lambdaQuery()
                .eq(UserPerms::getUserId, userId)
                .eq(UserPerms::getChannelNo, channelNo)
                .eq(UserPerms::getPermsType, UserConstant.PERM_TYPE_MENU));
        if (CollectionUtils.isNotEmpty(userPermsList)) {
            userHasPermMap = userPermsList.stream().collect(Collectors.toMap(UserPerms::getPermsId, o -> o != null, (ov, nv) -> ov && nv));
        }

        // 查询菜单
        return getPermMenus(userHasPermMap);
    }

    @Override
    public List<PermDetailDto> listPostJobTempMenu(String channelNo, Long postJobId, boolean isAdmin) {
        Map<Long, Boolean> postJobHasPermMap = new HashMap<>();

        // 查询岗位模板拥有的权限
        List<PostJobPerms> postJobPermsList = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery()
                .eq(PostJobPerms::getPostJobId, postJobId)
                .eq(PostJobPerms::getChannelNo, channelNo)
                .eq(PostJobPerms::getPermsType, UserConstant.PERM_TYPE_MENU));
        if (CollectionUtils.isNotEmpty(postJobPermsList)) {
            postJobHasPermMap = postJobPermsList.stream().collect(Collectors.toMap(PostJobPerms::getPermsId, o -> o != null, (ov, nv) -> ov && nv));
        }

        return getPermsMenuTree(channelNo, postJobHasPermMap, isAdmin);
    }

    @Override
    public List<PermDetailDto> flatListPostJobTempMenu(String channelNo, Long postJobId) {
        Map<Long, Boolean> postJobHasPermMap = new HashMap<>();

        // 查询岗位模板拥有的权限
        List<PostJobPerms> postJobPermsList = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery()
                .eq(PostJobPerms::getPostJobId, postJobId)
                .eq(PostJobPerms::getChannelNo, channelNo)
                .eq(PostJobPerms::getPermsType, UserConstant.PERM_TYPE_MENU));
        if (CollectionUtils.isNotEmpty(postJobPermsList)) {
            postJobHasPermMap = postJobPermsList.stream().collect(Collectors.toMap(PostJobPerms::getPermsId, o -> o != null, (ov, nv) -> ov && nv));
        }

        // 查询菜单
        return getPermMenus(postJobHasPermMap);
    }

    @Override
    public List<PermDto> listUserPermDetailByMenu(String channelNo, Long menuId, Long userId) {
        return permsDao.listUserPermDetailByMenu(channelNo, menuId, userId);
    }

    @Override
    public List<PermDto> listPostJobPermDetailByMenu(String channelNo, Long menuId, Long postJobId) {
        return permsDao.listPostJobPermDetailByMenu(channelNo, menuId, postJobId);
    }

    @Override
    @Cacheable(value = RedisConstant.REDIS_CACHE_USER_PERMS, key = "\"_\" + #channelNo + \"_\" + #userId")
    public List<PermDto> listUserAllPerms(String channelNo, Long userId) {
        return permsDao.listUserAllPerms(channelNo, userId);
    }

    @Override
    public List<Perms> getAll() {
        return permsDao.getAll();
    }

    @Override
    public Perms getByPermsCode(String permsCode) {
        return getOne(Wrappers.<Perms>lambdaQuery().eq(Perms::getPermsCode, permsCode));
    }

    /**
     * 查询权限菜单树
     * @param channelNo
     * @param hasPermMap
     * @param isAdmin
     */
    private List<PermDetailDto> getPermsMenuTree(String channelNo, Map<Long, Boolean> hasPermMap, boolean isAdmin) {
        List<PermDetailDto> permDetailList = new ArrayList<>();
        Map<Long, List<Perms>> childMap = new HashMap<>();

        // 查询一级菜单
        Wrapper<Perms> topQw = Wrappers.<Perms>lambdaQuery()
                .eq(Perms::getType, UserConstant.PERM_TYPE_MENU)
                .eq(Perms::getPId, -1)
                .orderByAsc(Perms::getSort);
        List<Perms> topMenus = Optional.ofNullable(list(topQw)).orElse(new ArrayList<>());
        List<Perms> childMenus = topMenus;
        List<Long> pIds;

        // 递归获取子菜单
        while (CollectionUtils.isNotEmpty(childMenus)) {
            // 记录权限拥有的子权限菜单
            for (Perms menu : childMenus) {
                Long pId = menu.getPId();
                List<Perms> childPerms = childMap.get(pId);
                if (childPerms == null) {
                    childPerms = new ArrayList<>();
                    childPerms.add(menu);
                    childMap.put(pId, childPerms);
                } else {
                    childPerms.add(menu);
                }
            }
            pIds = childMenus.stream().map(Perms::getId).distinct().collect(Collectors.toList());

            childMenus = getChildPerms(pIds, UserConstant.PERM_TYPE_MENU);
        }

        // 组织权限菜单树结构
        for (Perms menu : topMenus) {
            Long id = menu.getId();
            String code = menu.getCode();
            String name = menu.getName();
            Integer type = menu.getType();
            String group = menu.getPermsGroup();
            String permCode = menu.getPermsCode();
            boolean have = isAdmin || hasPermMap.getOrDefault(id, false);
            PermDetailDto permDetail = new PermDetailDto(id, code, name, type, have, group, permCode);
            permDetailList.add(permDetail);
            Map<Long, Boolean> finalUserHasPermMap = hasPermMap;
            List<PermDetailDto> children = childMap.getOrDefault(id, new ArrayList<>()).stream()
                    .map(o -> new PermDetailDto(o.getId(), o.getCode(), o.getName(), o.getType(),
                            isAdmin || finalUserHasPermMap.getOrDefault(o.getId(), false), o.getPermsGroup(), o.getPermsCode()))
                    .collect(Collectors.toList());
            permDetail.getChilds().addAll(children);
        }

        return permDetailList;
    }

    /**
     * 查询子权限
     * @param pIds
     * @param type
     * @return
     */
    private List<Perms> getChildPerms(List<Long> pIds, Integer type) {
        if (CollectionUtils.isEmpty(pIds)) {
            return null;
        }
        Wrapper<Perms> childQw = Wrappers.<Perms>lambdaQuery()
                .in(Perms::getPId, pIds)
                .eq(Perms::getType, type)
                .orderByAsc(Perms::getSort);
        return list(childQw);
    }

    /**
     * 查询权限菜单
     * @param hasPermMap
     * @return
     */
    private List<PermDetailDto> getPermMenus(Map<Long, Boolean> hasPermMap) {
        List<PermDetailDto> ret = new ArrayList<>();
        Wrapper<Perms> qw = Wrappers.<Perms>lambdaQuery()
                .eq(Perms::getType, UserConstant.PERM_TYPE_MENU)
                .orderByAsc(Perms::getSort);
        List<Perms> menus = Optional.ofNullable(list(qw)).orElse(new ArrayList<>());
        for (Perms menu : menus) {
            Long id = menu.getId();
            String code = menu.getCode();
            String name = menu.getName();
            Integer type = menu.getType();
            String group = menu.getPermsGroup();
            String permCode = menu.getPermsCode();
            boolean have = hasPermMap.getOrDefault(id, false);
            PermDetailDto permDetail = new PermDetailDto(id, code, name, type, have, group, permCode);
            ret.add(permDetail);
        }

        return ret;
    }

}

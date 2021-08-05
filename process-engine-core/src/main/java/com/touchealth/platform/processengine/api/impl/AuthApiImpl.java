package com.touchealth.platform.processengine.api.impl;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.entity.user.ResourcePerms;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.service.user.PermsService;
import com.touchealth.platform.processengine.service.user.ResourcePermsService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.process.engine.api.AuthApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.AuthConstant.PERMS_WHITELIST;

/**
 * 其他平台web api接口授权服务实现
 *
 * @author SY
 */
@Service("authApi")
@Slf4j
public class AuthApiImpl implements AuthApi {

    @Resource
    private PermsService permsService;
    @Resource
    private ResourcePermsService resourcePermsService;
    @Resource
    private UserService userService;

    @Override
    public Boolean isAuth(String channelNo, String resource, Long userId) {
        // 白名单接口无需权限验证
        for (String permWhitelist : PERMS_WHITELIST) {
            if (permWhitelist.equals(resource)) {
                return true;
            }
        }

        AntPathMatcher matcher = new AntPathMatcher();
        String permsCodes = null;

        List<ResourcePerms> resourcePerms = resourcePermsService.listAll(CommonConstant.APP_NAME);
        if (!CollectionUtils.isEmpty(resourcePerms)) {
            for (ResourcePerms rp : resourcePerms) {
                if (matcher.match(rp.getResource(), resource)) {
                    permsCodes = rp.getPerms();
                }
            }
        }

        // 未定义的资源权限认为是无需权限验证的
        if (permsCodes != null) {
            User user = userService.findById(userId);
            if (user == null) {
                log.error("invalid user id. {}", userId);
                return false;
            }

            List<PermDto> perms = null;
            // 超级管理员拥有所有权限
            if (UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
                // TODO 20200121 结果加入缓存
                List<Perms> allPerms = permsService.list();
                if (!CollectionUtils.isEmpty(allPerms)) {
                    perms = allPerms.stream().map(o -> PermDto.builder()
                            .id(o.getId())
                            .code(o.getCode())
                            .permsCode(o.getPermsCode())
                            .name(o.getName())
                            .have(true)
                            .group(o.getPermsGroup())
                            .type(o.getType())
                            .build()).collect(Collectors.toList());
                }
            } else {
                perms = permsService.listUserAllPerms(channelNo, userId);
            }

            if (CollectionUtils.isEmpty(perms)) {
                return false;
            }

            Map<String, Boolean> permsHaveMap = perms.stream().collect(Collectors.toMap(
                    PermDto::getPermsCode, o -> Boolean.TRUE, (ov, nv) -> ov));
            String[] permsCodeArr = permsCodes.split(String.valueOf(StringUtils.DEFAULT_DELIMITER_CHAR));
            // 若资源配置了多个权限，那么只要用户拥有其中的一个就可以访问
            for (String permsCode : permsCodeArr) {
                if (permsHaveMap.getOrDefault(permsCode, false)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

}

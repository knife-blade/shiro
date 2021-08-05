package com.touchealth.platform.processengine.shiro;

import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.handler.ChannelHandler;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.user.PermsService;
import com.touchealth.platform.processengine.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.KEY_SPLIT;
import static com.touchealth.platform.processengine.shiro.CustomFormAuthenticationFilter.LOGIN_VERIFY_CODE;

/**
 * 自定义Realm
 *
 * @author SunYang
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private PermsService permsService;
    @Autowired
    private PlatformChannelService platformChannelService;

    /**
     * 接口授权时调用，返回授权信息
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        String principal = String.valueOf(principals.getPrimaryPrincipal());
        if (StringUtils.isEmpty(principal)) {
            return simpleAuthorizationInfo;
        }

        String username = principal;

        List<PermDto> perms = new ArrayList<>();
        User user = null;

        // 验证码登录
        if (username.startsWith(LOGIN_VERIFY_CODE)) {
            String mobileNo = username.substring(LOGIN_VERIFY_CODE.length());
            // TODO 20200121 结果加入缓存
            List<User> userList = userService.findByMobileAndType(mobileNo, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
            if (CollectionUtils.isNotEmpty(userList)) {
                user = userList.get(0);
            }
        }
        // 邮箱登录
        else {
            // TODO 20200121 结果加入缓存
            user = userService.findByEmail(username, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
        }

        if (user == null) {
            log.warn("CustomRealm.doGetAuthorizationInfo user not exits {}", username);
            return simpleAuthorizationInfo;
        }
        String channelNo = user.getChannelNo();
        // 超级管理员拥有所有权限
        if (UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
            // TODO 20200121 结果加入缓存
            List<Perms> allPerms = permsService.list();
            if (CollectionUtils.isNotEmpty(allPerms)) {
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
            // 查询用户所有权限
            perms = permsService.listUserAllPerms(ChannelHandler.getChannelNo(), user.getId());
        }
        if (CollectionUtils.isNotEmpty(perms)) {
            for (PermDto perm : perms) {
                simpleAuthorizationInfo.addStringPermission(perm.getPermsCode());
            }
        }

        return simpleAuthorizationInfo;
    }

    /**
     * 认证时调用，返回认证信息
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            String principal = String.valueOf(token.getPrincipal());
            String credential = "";
            if (token.getCredentials() instanceof char[]) {
                char[] credentials = (char[]) token.getCredentials();
                credential = String.valueOf(credentials);
            }

            if (LOGIN_VERIFY_CODE.equals(credential)) {
                return new SimpleAuthenticationInfo(principal, token.getCredentials(), getName());
            } else {
                String[] usernameAndChannelNo = principal.split(KEY_SPLIT);
                String username = usernameAndChannelNo[0];

                User user = userService.findByEmail(username, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
                if (user == null) {
                    return null;
                }
                return new SimpleAuthenticationInfo(principal, user.getPassword(), getName());
            }
        } catch (Exception e) {
            log.error("CustomRealm has error", e);
            return new SimpleAuthenticationInfo("", "", getName());
        }
    }

}

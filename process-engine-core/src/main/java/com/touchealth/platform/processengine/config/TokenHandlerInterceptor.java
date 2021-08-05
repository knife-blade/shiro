package com.touchealth.platform.processengine.config;

import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.constant.GlobalDefine;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.entity.user.UserChannel;
import com.touchealth.platform.processengine.entity.user.UserLoginHistory;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.user.UserChannelService;
import com.touchealth.platform.processengine.service.user.UserLoginHistoryService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.JwtUtil;
import com.touchealth.platform.user.client.api.LoginApi;
import com.touchealth.platform.user.client.dto.request.CheckTokenReq;
import com.touchealth.platform.user.client.dto.response.LoginUserRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

import static com.touchealth.platform.processengine.constant.CommonConstant.SC_CHANNEL_ID;
import static com.touchealth.platform.processengine.constant.PageCenterConsts.MOBILE_URI_TAG;

/**
 * token校验
 *
 * @author liufengqiang
 * @date 2020-12-16 14:02:42
 */
@Component
@RefreshScope
@Slf4j
public class TokenHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserLoginHistoryService userLoginHistoryService;
    @Autowired
    private UserChannelService userChannelService;
    @Autowired
    private PlatformChannelService platformChannelService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginApi loginApi;

    @Value("#{@environment['user.login2'] ?: false}")
    private Boolean login2;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        request.setAttribute("basePath", basePath);
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 免登陆
        if (method.isAnnotationPresent(PassToken.class)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        // 暂定mobile开头的接口为移动端接口，其他为pc端接口
        boolean isMobile = request.getRequestURI().replaceAll("//", "/").startsWith(MOBILE_URI_TAG);
        Long userId = null;

        try {
            /* 登录认证 */
            if (isMobile && Optional.ofNullable(login2).orElse(false)) {
                // 流程引擎H5对接用户中台的登录认证
                String rentId = request.getHeader("Rent-Id");
                if (StringUtils.isEmpty(rentId)) {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().append(HttpStatus.BAD_REQUEST.getReasonPhrase());
                    return false;
                }
                CheckTokenReq tokenReq = new CheckTokenReq();
                tokenReq.setIp(ServletUtil.getClientIP(request));
                tokenReq.setRentId(Long.parseLong(rentId));
                tokenReq.setToken(authorization);
                PlatFormResponse<LoginUserRes> checkRes = loginApi.checkTokenAndGetUser(tokenReq); // TODO 20210420 暂时每次都去中台验证，可能会对所有接口响应时长有影响
                LoginUserRes userInfo = checkRes.getDataOrThrowError();
                if (userInfo == null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    return false;
                }
                userId = userInfo.getId();
                request.setAttribute("token", authorization);
                request.setAttribute("userId", userId);
            } else {
                // 流程引擎自己登录认证
                if (StringUtils.isEmpty(authorization) || !authorization.startsWith(GlobalDefine.Jwt.TOKEN_PREFIX)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    return false;
                }
                // 去除Base 后部分
                final String token = authorization.substring(GlobalDefine.Jwt.TOKEN_PREFIX.length());

                Map userInfo = JwtUtil.getUserInfo(token);
                String userType = Optional.ofNullable(userInfo.get("userType")).orElse("").toString();
                String currentUserUniqueMark = Optional.ofNullable(userInfo.get("currentUserUniqueMark")).orElse("").toString();
                boolean isUnauthorized = (isMobile && !userType.equals(UserConstant.USER_TYPE_PROCESS_ENGINE_H5.toString()))
                        || (!isMobile && !userType.equals(UserConstant.USER_TYPE_PROCESS_ENGINE.toString()) && !userType.equals(UserConstant.USER_TYPE_PROCESS_ENGINE_SUPER.toString()));
                if (isUnauthorized) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    return false;
                }
                userId = JwtUtil.getUserId(token);
                request.setAttribute("token", token);
                request.setAttribute("userId", userId);

                // h5用户只能单端登录
                if(isMobile){
                    // 获取最新登录历史
                    UserLoginHistory userLoginHistory = userLoginHistoryService.getByUserIdAndUserType(userId, UserConstant.USER_TYPE_PROCESS_ENGINE_H5);

                    if(null != userLoginHistory && !userLoginHistory.getCurrentUserRemark().equals(currentUserUniqueMark)){
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().append("已在其他设备登录");
                        return false;
                    }
                }
            }

            /*
             * 验证用户是否拥有渠道权限，若用户拥有主平台权限，那么拥有所有平台权限
             */
            if(!isMobile && !isGetChannelApi(request)) {
                PlatformChannel mainPlatform = platformChannelService.getById(SC_CHANNEL_ID);
                String mainChannelNo = mainPlatform == null ? "" : mainPlatform.getChannelNo();
                String channelNo = request.getHeader("channelNo");
                User user = userService.findById(userId);
                if (user == null ||
                        UserConstant.STAFF_STATUS.OUT.getCode().equals(user.getStaffStatus())) {
                    log.error("TokenHandlerInterceptor user not exit. {}", userId);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    return false;
                }
                String userChannelNo = user.getChannelNo();
                Set<String> hasChannelList = new HashSet<>();
                if (StringUtils.isNotEmpty(userChannelNo)) {
                    hasChannelList.add(userChannelNo);
                }

                List<UserChannel> channelList = userChannelService.list(Wrappers.<UserChannel>lambdaQuery().eq(UserChannel::getUserId, userId));
                if (!CollectionUtils.isEmpty(channelList)) {
                    String channelNoList = Optional.ofNullable(channelList.get(0).getChannelNoList()).orElse("");
                    String[] channelNos = channelNoList.split(",");
                    for (String o : channelNos) {
                        if (StringUtils.isNotEmpty(o)) {
                            hasChannelList.add(o);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(hasChannelList)) {
                    log.error("TokenHandlerInterceptor user has not permission for this channel");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().append(HttpStatus.FORBIDDEN.getReasonPhrase());
                    return false;
                }
                // 势成主平台渠道超级管理员拥有所有渠道权限
                if (StringUtils.isNotEmpty(mainChannelNo) && mainChannelNo.equals(userChannelNo) &&
                        UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
                    return true;
                }
                if (!hasChannelList.contains(channelNo)) {
                    log.error("TokenHandlerInterceptor user has not permission for this channel. has channel {}", hasChannelList);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().append(HttpStatus.FORBIDDEN.getReasonPhrase());
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("鉴权失败", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().append("Invalid token");
            return false;
        }

        return true;
    }

    /**
     * 是否是获取渠道接口，获取渠道接口无需认证用户是否拥有该渠道权限，因为此时还没选渠道
     * @param request
     * @return
     */
    private boolean isGetChannelApi(HttpServletRequest request) {
        String requestURI = request.getRequestURI().replaceAll("//", "/");
        String method = request.getMethod();
        return PageCenterConsts.GET_CHANNEL_URI.equals(requestURI) && HttpMethod.GET.matches(method);
    }

}

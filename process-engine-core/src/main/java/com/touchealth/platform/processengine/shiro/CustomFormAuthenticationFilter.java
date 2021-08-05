package com.touchealth.platform.processengine.shiro;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.exception.ErrorMsgException;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.request.user.LoginRequest;
import com.touchealth.platform.processengine.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 自定义“authc”认证过滤器
 *
 * @author SunYang
 */
@Slf4j
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    /**
     * Shiro登录认证最大过期时间毫秒值。
     */
    private static final Long MAX_LOGIN_IDLE_TIME_IN_MILLIS = 365 * 24 * 3600_000L;

    public static String LOGIN_VERIFY_CODE = "VERIFY_CODE";

    @Autowired
    private UserService userService;
    @Autowired
    private ShiroFilterChainDefinition shiroFilterChainDefinition;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest) {
            String requestURI = ((HttpServletRequest) request).getRequestURI();
            PatternMatcher pathMatcher = new AntPathMatcher();
            // 若配置了匿名访问（anon）权限，那么直接跳过，不进行权限验证
            for (Map.Entry<String, String> entry : shiroFilterChainDefinition.getFilterChainMap().entrySet()) {
                String url = entry.getKey();
                String auth = entry.getValue();
                if ("anon".equals(auth) && pathMatcher.matches(url, requestURI)) {
                    return true;
                }
            }
        }

        if (isLoginRequest(request, response)) {
            Subject subject = getSubject(request, response);
            if (subject != null) {
                subject.logout();
            }
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            } else {
                return true;
            }
        } else {
            // 返回401状态码
            responseUnauthorized(response);
            return false;
        }
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        try {
            AuthenticationToken token = createToken(request, response);
            if (token == null) {
                String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                        "must be created in order to execute a login attempt.";
                log.error("CustomFormAuthenticationFilter.executeLogin username incorrect. {}", msg);
                responseMsg(response, 1, "账号密码错误");
                return false;
            }

            Subject subject = getSubject(request, response);
            subject.getSession().setTimeout(MAX_LOGIN_IDLE_TIME_IN_MILLIS);
            subject.login(token);
            return true;
        } catch (IncorrectCredentialsException e) {
            log.error("CustomFormAuthenticationFilter.executeLogin password incorrect", e);
            responseMsg(response, 1, "账号密码错误");
            return false;
        } catch (AuthenticationException e) {
            log.error("CustomFormAuthenticationFilter.executeLogin auth fail", e);
            // 返回401状态码
            responseUnauthorized(response);
            return false;
        } catch (ErrorMsgException e) {
            responseMsg(response, 1, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("CustomFormAuthenticationFilter.executeLogin has error", e);
            return false;
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws ErrorMsgException {
        try {
            ShiroHttpServletRequest wrapperRequest = (ShiroHttpServletRequest) request;
            String body = wrapperRequest.getReader().readLine();
            if (StringUtils.isEmpty(body)) {
                return null;
            }

            String username, password;

            LoginRequest loginRequest = JSONObject.parseObject(body, LoginRequest.class);

            // 账号密码登录
            if (UserConstant.USER_SIGN_TYPE_4_ACCESS == loginRequest.getLoginType()) {
                username = loginRequest.getEmail();
                password = loginRequest.getPassword();
                User user = userService.findByEmail(username, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
                if (user == null) {
                    return null;
                }
//                if (CommonConstant.IS_DELETE == Optional.ofNullable(user.getIsDel()).orElse(Long.valueOf(CommonConstant.IS_DELETE)).intValue()) {
//                    throw new ErrorMsgException("您的账号已被移除，请联系后台管理员");
//                }
                password = DigestUtil.md5Hex(DigestUtil.md5Hex(password) + user.getSalt());
            }
            // 短信验证码登录
            else {
                username = LOGIN_VERIFY_CODE + loginRequest.getMobile();
                password = LOGIN_VERIFY_CODE;
            }
            return createToken(username, password, request, response);
        } catch (JSONException e) {
            log.error("CustomFormAuthenticationFilter.createToken param error", e);
        } catch (IOException e) {
            log.error("CustomFormAuthenticationFilter.createToken has IO error", e);
        }
        return null;
    }

    /**
     * 响应客户端401未授权
     * @param response
     * @throws IOException
     */
    private void responseUnauthorized(ServletResponse response) throws IOException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        } else {
            log.warn("CustomFormAuthenticationFilter.responseUnauthorized 不太正常的响应");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().append(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }

    /**
     * 响应客户端自定义消息
     * @param response
     * @param status
     * @param msg
     * @throws IOException
     */
    private void responseMsg(ServletResponse response, Integer status, String msg) throws IOException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpStatus.OK.value());
            Response<Object> error = Response.error(status, msg);
            httpServletResponse.getWriter().append(JSONObject.toJSONString(error));
        }
    }

}

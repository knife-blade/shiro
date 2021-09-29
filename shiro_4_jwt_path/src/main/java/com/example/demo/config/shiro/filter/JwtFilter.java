package com.example.demo.config.shiro.filter;

import com.example.demo.common.util.ResponseUtil;
import com.example.demo.common.util.auth.JwtUtil;
import com.example.demo.config.shiro.entity.JwtToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtFilter extends AuthenticatingFilter {
    /**
     * 所有请求都会到这里来（无论是不是anon）。
     * 返回true：表示允许向下走。后边会走PathMatchingFilter，看路径是否对应anon等
     * 返回false：表示不允许向下走。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest,
                                     ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(HttpHeaders.COOKIE);
        // 自定义Header也可以，但浏览器不会存自定义的Header，需要前端自己去存
        // String token = request.getHeader("Authentication");

        if (!StringUtils.hasText(token)) {
            return true;
        } else {
            boolean verified = JwtUtil.verifyToken(token);
            if (!verified) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                ResponseUtil.jsonResponse(response, HttpStatus.UNAUTHORIZED.value(), "认证失败");
                return false;
            }
        }

        // 此登录并非调用login接口，而是shiro层面的登录。
        // 里边会调用下边的createToken方法
        return executeLogin(servletRequest, servletResponse);
    }

    /**
     * 这里的token会传给AuthorizingRealm子类（本处是AccountRealm）的doGetAuthenticationInfo方法作为参数
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest,
                                              ServletResponse servletResponse) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(HttpHeaders.COOKIE);
        // 自定义Header也可以，但浏览器不会存自定义的Header，需要前端自己去存
        // String token = request.getHeader("Authentication");
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return new JwtToken(token);
    }
}

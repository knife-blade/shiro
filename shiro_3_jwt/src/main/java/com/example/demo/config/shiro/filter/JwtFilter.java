package com.example.demo.config.shiro.filter;

import com.example.demo.common.constant.AuthConstant;
import com.example.demo.common.utils.JwtUtil;
import com.example.demo.config.shiro.entity.JwtToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class JwtFilter extends AuthenticatingFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest,
                                     ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(AuthConstant.TOKEN_HEADER);

        if (!StringUtils.hasText(token)) {
            return true;
        } else {
            boolean verified = JwtUtil.verifyToken(token);
            if (!verified) {
                return true;
            }
        }

        return executeLogin(servletRequest, servletResponse);
    }

    //这里的token会传给AuthorizingRealm子类（本处是DatabaseRealm）的doGetAuthenticationInfo方法作为参数
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest,
                                              ServletResponse servletResponse) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(AuthConstant.TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return new JwtToken(token);
    }


}

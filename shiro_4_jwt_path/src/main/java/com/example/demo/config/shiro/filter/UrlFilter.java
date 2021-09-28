package com.example.demo.config.shiro.filter;

import com.example.demo.common.entity.Result;
import com.example.demo.common.util.ApplicationContextHolder;
import com.example.demo.common.util.auth.JwtUtil;
import com.example.demo.rbac.permission.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class UrlFilter extends PathMatchingFilter {
    private PermissionService permissionService;

    @Override
    protected boolean onPreHandle(ServletRequest request,
                                  ServletResponse response,
                                  Object mappedValue) throws Exception {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String uri = httpServletRequest.getRequestURI();
        String token = httpServletRequest.getHeader(HttpHeaders.COOKIE);
        String userIdStr = JwtUtil.getUserIdByToken(token);
        Long userId = Long.parseLong(userIdStr);

        if (permissionService == null) {
            permissionService = ApplicationContextHolder.getContext().getBean(PermissionService.class);
        }
        Set<String> permissions = permissionService.getPermissionsByUserId(userId);

        // 实际应该从数据库或者redis里通过userId获得拥有权限的url
        if (permissions.contains(uri)) {
            return true;
        }

        // 构造无权限时的response
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //让浏览器用utf8来解析返回的数据
        httpResponse.setHeader("Content-type", "text/html;charset=UTF-8");
        //告诉servlet用UTF-8转码，而不是用默认的ISO8859
        httpResponse.setCharacterEncoding("UTF-8");

        Result result = new Result().failure().message("用户(" + userId + ")无此url(" + uri + ")权限");
        String json = new ObjectMapper().writeValueAsString(result);
        httpResponse.getWriter().print(json);

        return false;
    }
}
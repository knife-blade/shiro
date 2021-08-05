package com.touchealth.platform.processengine.shiro;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.handler.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义权限过滤器
 *
 * @author SunYang
 */
@Slf4j
public class CustomPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    private static final String DEFAULT_PATH_SEPARATOR = "/";
    @Autowired
    private ShiroFilterChainDefinition shiroFilterChainDefinition;

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
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

        if (request instanceof HttpServletRequest) {
            String channelNo = ((HttpServletRequest) request).getHeader(CommonConstant.HEADER_CHANNEL);
            ChannelHandler.setChannelNo(channelNo);
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean pathsMatch(String path, ServletRequest request) {
        String requestURI = getPathWithinApplication(request);
        if (requestURI != null && !DEFAULT_PATH_SEPARATOR.equals(requestURI)
                && requestURI.endsWith(DEFAULT_PATH_SEPARATOR)) {
            requestURI = requestURI.substring(0, requestURI.length() - 1);
        }
        if (path != null && !DEFAULT_PATH_SEPARATOR.equals(path)
                && path.endsWith(DEFAULT_PATH_SEPARATOR)) {
            path = path.substring(0, path.length() - 1);
        }
        log.trace("CustomPermissionsAuthorizationFilter => Attempting to match pattern '{}' with current requestURI '{}'...", path, Encode.forHtml(requestURI));

        // 处理请求方法
        if (path.matches("^(GET|PUT|DELETE|POST|OPTIONS) .*") && request instanceof HttpServletRequest) {
            String method = ((HttpServletRequest) request).getMethod();
            requestURI = method.toUpperCase() + " " + requestURI;
        }

        return pathsMatch(path, requestURI);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            String unauthorizedUrl = getUnauthorizedUrl();
            if (StringUtils.hasText(unauthorizedUrl)) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
            } else {
                responseForbidden(response);
            }
        }
        return false;
    }

    /**
     * 响应客户端403不可访问
     * @param response
     * @throws IOException
     */
    private void responseForbidden(ServletResponse response) throws IOException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.getWriter().append(HttpStatus.FORBIDDEN.getReasonPhrase());
        } else {
            log.warn("CustomPermissionsAuthorizationFilter.responseForbidden 不太正常的响应");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().append(HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

}

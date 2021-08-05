package com.touchealth.platform.processengine.config;


import com.touchealth.platform.processengine.constant.CommonConstant;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*",filterName = "channelFilter")
public class ChannelFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if (httpServletRequest.getContentType() == null || !httpServletRequest.getContentType().startsWith(CommonConstant.FORM_CONTENT_TYPE)) {
            ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(httpServletRequest);
            filterChain.doFilter(requestWrapper, servletResponse);
        } else {
            filterChain.doFilter(httpServletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }
}

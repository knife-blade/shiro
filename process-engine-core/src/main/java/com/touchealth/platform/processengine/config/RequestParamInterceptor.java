package com.touchealth.platform.processengine.config;

import cn.hutool.extra.emoji.EmojiUtil;
import com.touchealth.platform.processengine.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class RequestParamInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 修改编辑都是通过requestBody请求的，获取请求body判断是否含有表情符号(可自定义注解是否需要拦截)
        try {
            // 非表单请求对数据非法字符进行验证
            if (request.getContentType() == null || !request.getContentType().startsWith(CommonConstant.FORM_CONTENT_TYPE)) {
                String bodyString = new BodyReaderHttpServletRequestWrapper(request).getBodyString();
                if (StringUtils.isNotEmpty(bodyString) && EmojiUtil.containsEmoji(bodyString)) {
                    throw new IllegalArgumentException("不能输入非法字符");
                }
            }
        }
        catch (IOException e1) {
            log.error("获取请求参数失败");
        }

        return true;
    }
}

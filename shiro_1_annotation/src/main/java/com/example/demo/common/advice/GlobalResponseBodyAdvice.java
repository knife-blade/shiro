package com.example.demo.common.advice;

import com.example.demo.common.constant.WhiteList;
import com.example.demo.common.entity.ResultWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 若接口返回的类型本身就是ResultWrapper，则无需操作，返回false
        // return !returnType.getParameterType().equals(ResultWrapper.class);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 将String转换，再将数据包装在ResultWrapper里
                objectMapper.writeValueAsString(body);
                return ResultWrapper.success();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化String返回类型错误");
            }
        } else if (body instanceof ResultWrapper) {
            return body;
        } else if (isKnife4jUrl(request.getURI().getPath())) {
            return body;
        }
        return ResultWrapper.success().data(body);
    }

    private boolean isKnife4jUrl(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String s : WhiteList.KNIFE4J) {
            if (pathMatcher.match(s, uri)) {
                return true;
            }
        }
        return false;
    }
}
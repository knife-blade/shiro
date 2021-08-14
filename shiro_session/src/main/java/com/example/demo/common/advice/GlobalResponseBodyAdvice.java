package com.example.demo.common.advice;

import com.example.demo.common.entity.ResultWrapper;
import com.example.demo.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private List<String> KNIFE4J_URI = Arrays.asList(
            "/doc.html",
            "/swagger-resources",
            "/swagger-resources/configuration",
            "/v3/api-docs",
            "/v2/api-docs");
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 若接口返回的类型本身就是ResultWrapper，则无需操作，返回false
        // return !returnType.getParameterType().equals(ResultWrapper.class);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            // 若返回值为String类型，需要包装为String类型返回。否则会报错
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ResultWrapper<String> result = ResultWrapper.success().data(body);
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化String返回类型错误");
            }
        } else if (body instanceof ResultWrapper) {
            return body;
        } else if (isKnife4jUrl(request.getURI().getPath())) {
            // 如果是接口文档uri，直接跳过
            return body;
        }
        return ResultWrapper.success().data(body);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultWrapper<Object> handleException(Exception e){
        log.error(e.getMessage(), e);

        if (e instanceof BusinessException) {
            return ResultWrapper.failure().message(e.getMessage());
        } else {
            // 实际项目中应该这样写，防止用户看到详细的异常信息
            // return ResultWrapper.failure().message("操作失败");
            return ResultWrapper.failure().message(e.getMessage());
        }
    }


    private boolean isKnife4jUrl(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String s : KNIFE4J_URI) {
            if (pathMatcher.match(s, uri)) {
                return true;
            }
        }
        return false;
    }
}
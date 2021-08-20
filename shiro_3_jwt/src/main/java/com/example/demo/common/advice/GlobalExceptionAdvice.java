package com.example.demo.common.advice;

import com.example.demo.common.entity.ResultWrapper;
import com.example.demo.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public ResultWrapper handleException(Exception e) {
        log.error(e.getMessage(), e);

        if (e instanceof BusinessException) {
            return ResultWrapper.failure().message(e.getMessage());
        }

        // 实际项目中应该这样写，防止用户看到详细的异常信息
        // return ResultWrapper.failure().message("操作失败");
        return ResultWrapper.failure().message(e.getMessage());
    }
}
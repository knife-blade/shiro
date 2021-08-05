package com.touchealth.platform.processengine.exception;

import com.touchealth.platform.processengine.pojo.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 参数校验异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response validationBodyException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<ObjectError> objectErrorList = result.getAllErrors();
            if(!CollectionUtils.isEmpty(objectErrorList)){
                errorMessage.append(objectErrorList.get(0).getDefaultMessage());
            }

            return StringUtils.isBlank(errorMessage.toString()) ? Response.illegalArgumentError : Response.error(errorMessage.toString());

        }

        //其他错误
        return Response.sysError;
    }

    @ExceptionHandler(value = BindException.class)
    public Response globalExceptionHandler(BindException bindException) {
        log.error("参数异常:", bindException);
        if (bindException.getBindingResult().getAllErrors().isEmpty()) {
            return Response.sysError;
        }
        return Response.error(bindException.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }


    @ExceptionHandler(value = Exception.class)
    public Response globalExceptionHandler(Exception e) {
        log.error("系统异常:", e);
        return Response.sysError;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Response globalExceptionHandler(IllegalArgumentException e) {
        log.error("断言异常", e);
        return Response.error(e.getMessage());
    }

    @ExceptionHandler(value = BusinessException.class)
    public Response globalExceptionHandler(BusinessException e) {
        log.error("业务异常", e);
        return Response.error(e.getMessage());
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public Response globalExceptionHandler(MissingRequestHeaderException e) {
        log.error("缺少必传请求头", e);
        return Response.error("缺少必传请求头");
    }

    @ExceptionHandler(value = ParameterException.class)
    public Response globalExceptionHandler(ParameterException e) {
        log.error("缺少必传参数", e);
        return Response.error("缺少必传参数");
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public Response globalExceptionHandler(UnauthorizedException e) {
        log.error("无权限", e);
        return Response.error(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

}

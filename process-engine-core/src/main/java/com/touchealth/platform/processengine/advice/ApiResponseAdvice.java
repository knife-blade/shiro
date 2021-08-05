package com.touchealth.platform.processengine.advice;

import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.annotation.NoApiResponse;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一处理返回值
 */
@ControllerAdvice(basePackages = {"com.touchealth.platform.processengine.controller","com.touchealth.platform.processengine.controllermobile"})
public class ApiResponseAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // TODO 20201229 根据用户权限过滤数据


        // 如果方法带有该注解的，不统一处理返回值
        NoApiResponse noApiResponse = methodParameter.getMethodAnnotation(NoApiResponse.class);
        if (null != noApiResponse) {
            return body;
        }
        // 如果返回体就是Response不处理
        if(body instanceof Response){
            return body;
        }
        // 如果返回体分页是pageInfo统一转成pageData
        if(body instanceof PageInfo){
            PageInfo pageInfo = (PageInfo) body;
            PageData pageData = new PageData(pageInfo.getPageNum(),pageInfo.getPageSize(),
                    (int)pageInfo.getTotal(),pageInfo.getPages(),pageInfo.getList());
            return Response.ok( pageData);
        }

        return Response.ok( body);
    }
}

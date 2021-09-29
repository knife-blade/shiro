package com.example.demo.common.util;



import com.example.demo.common.entity.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtil {
    public static void jsonResponse(HttpServletResponse response, int status, String message) throws Exception {
        //让浏览器用utf8来解析返回的数据
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        //告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);

        Result result = new Result().failure().message(message);
        String json = new ObjectMapper().writeValueAsString(result);
        response.getWriter().print(json);
    }
}

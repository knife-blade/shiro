package com.example.demo.common.constant;

import org.springframework.http.HttpHeaders;

public interface AuthConstant {
    String ALGORITHM_TYPE = "md5";
    int HASH_ITERATIONS = 2;
    String AUTHENTICATION_HEADER = HttpHeaders.COOKIE;
}

package com.example.demo.common.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface WhiteList {
    List<String> KNIFE4J = Arrays.asList(
            "/swagger-resources",
            "/swagger-resources/configuration",
            "/v3/api-docs",
            "/v2/api-docs");

    List<String> ALL = new ArrayList<>(KNIFE4J);
}

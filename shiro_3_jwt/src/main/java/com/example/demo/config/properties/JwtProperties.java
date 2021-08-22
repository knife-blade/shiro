package com.example.demo.config.properties;

import com.example.demo.common.utils.JwtUtil;
import lombok.Data;

@Data
public class JwtProperties {
    private String secret;
    private long expire;

    public void setForJwtUtil() {
        JwtUtil.setJwtProperties(this);
    }
}

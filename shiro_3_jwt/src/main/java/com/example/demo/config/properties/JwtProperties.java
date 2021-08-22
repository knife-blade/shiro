package com.example.demo.config.properties;

import lombok.Data;

@Data
public class JwtProperties {
    private String secret;
    private long expire;
}

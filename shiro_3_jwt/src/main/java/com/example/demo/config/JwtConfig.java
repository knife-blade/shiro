package com.example.demo.config;

import com.example.demo.config.properties.JwtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean(initMethod = "setForJwtUtil")
    @ConfigurationProperties(prefix = "custom.jwt")
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }
}

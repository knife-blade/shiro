package com.example.demo.config.shiro.entity;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JwtToken代替官方的UsernamePasswordToken，是Shiro用户名、密码等信息的载体，
 * 前后端分离，服务器不保存用户状态，所以不需要RememberMe等功能。
 */
public class JwtToken implements AuthenticationToken {

    private final String token;

    public JwtToken(String jwt) {
        this.token = jwt;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
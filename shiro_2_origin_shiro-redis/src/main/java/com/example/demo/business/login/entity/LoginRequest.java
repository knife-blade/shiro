package com.example.demo.business.login.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    private String userName;
    private String password;
}

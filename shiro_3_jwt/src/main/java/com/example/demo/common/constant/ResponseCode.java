package com.example.demo.common.constant;

public enum ResponseCode {
    SUCCESS(1000, "访问成功"),
    SYSTEM_FAILURE(1001, "系统异常"),
    ;

    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

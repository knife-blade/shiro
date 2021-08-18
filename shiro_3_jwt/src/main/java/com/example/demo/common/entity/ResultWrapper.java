package com.example.demo.common.entity;

import com.example.demo.common.constant.ResponseCode;
import lombok.Data;

@Data
public class ResultWrapper<T> {
    private boolean success;

    private int code;

    private String message;

    private T data;

    public ResultWrapper() {
    }

    public ResultWrapper(boolean success) {
        this.success = success;

    }

    @SuppressWarnings({"rawtypes"})
    public static <R extends ResultWrapper> R success(boolean success) {
        if (success) {
            return success();
        } else {
            return failure();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R success() {
        return (R) success(true).code(ResponseCode.SUCCESS.getCode());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R failure() {
        return (R) success(false).code(ResponseCode.SYSTEM_FAILURE.getCode());
    }

    /**
     * @param code {@link ResponseCode#getCode()}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <R extends ResultWrapper> R code(int code) {
        this.code = code;
        return (R) this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <R extends ResultWrapper> R message(String message) {
        this.message = message;
        return (R) this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <R extends ResultWrapper> R data(T data) {
        this.data = data;
        return (R) this;
    }
}
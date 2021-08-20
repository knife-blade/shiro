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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R success(boolean success) {
        ResultWrapper<R> resultWrapper = new ResultWrapper<>(success);
        if (success) {
            resultWrapper.code = ResponseCode.SUCCESS.getCode();
        } else {
            resultWrapper.code = ResponseCode.SYSTEM_FAILURE.getCode();
        }
        return (R) resultWrapper;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R success() {
        return (R) success(true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R failure() {
        return (R) success(false);
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
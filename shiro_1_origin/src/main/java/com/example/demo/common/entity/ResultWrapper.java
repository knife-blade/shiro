package com.example.demo.common.entity;

import lombok.Data;

@Data
public class ResultWrapper<T> {
    private boolean success;

    private String message;

    private T data;

    public ResultWrapper() {
    }

    public ResultWrapper(boolean success) {
        this.success = success;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends ResultWrapper> R success(boolean success) {
        return (R) new ResultWrapper(success);
    }

    @SuppressWarnings({"rawtypes"})
    public static <R extends ResultWrapper> R success() {
        return success(true);
    }

    @SuppressWarnings({"rawtypes"})
    public static <R extends ResultWrapper> R failure() {
        return success(false);
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
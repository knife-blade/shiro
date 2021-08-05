package com.touchealth.platform.processengine.exception;

/**
 * 参数异常
 */
public class ParameterException extends RuntimeException {

    public ParameterException() {
    }

    public ParameterException(String message) {
        super(message);
    }
}

package com.touchealth.platform.processengine.exception;

/**
 * 通用组件异常
 *
 * @author SunYang
 */
public class CommonModuleException extends RuntimeException {

    public CommonModuleException() {
    }

    public CommonModuleException(String message) {
        super(message);
    }
}

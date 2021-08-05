package com.touchealth.platform.processengine.exception;

/**
 * 业务异常
 *
 * @author liufengqiang
 * @date 2020-11-23 15:53:31
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

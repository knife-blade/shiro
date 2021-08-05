package com.touchealth.platform.processengine.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorMsgException extends RuntimeException {

    public ErrorMsgException(String message) {
        super(message);
    }
}

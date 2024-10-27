package com.devita.common.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {

    private final ErrorCode errorCode;

    public AccessDeniedException(String message) {
        super(message);
        this.errorCode = ErrorCode.ACCESS_DENIED;
    }

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
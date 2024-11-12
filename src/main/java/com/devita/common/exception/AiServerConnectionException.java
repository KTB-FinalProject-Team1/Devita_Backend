package com.devita.common.exception;

import lombok.Getter;

@Getter
public class AiServerConnectionException extends RuntimeException{
    private final ErrorCode errorCode;

    public AiServerConnectionException(String message) {
        super(message);
        this.errorCode = ErrorCode.ACCESS_DENIED;
    }

    public AiServerConnectionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

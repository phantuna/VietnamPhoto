package com.example.backend.exception;

public class AppException extends RuntimeException {

    private ErrorCode errorCode;
    private Object data;

    public AppException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Object data) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.data = data;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }
}


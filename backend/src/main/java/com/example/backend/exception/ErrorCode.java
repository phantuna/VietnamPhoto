package com.example.backend.exception;

public enum ErrorCode {

    JWT_NOT_CREATED(1000, "jwt chua tao"),
    INVALID_SIGNATURE(1001, "sai chư ky "),
    INVALID_TOKEN(1002, "sai token"),
    MISSING_TOKEN(1003, "khong co token"),
    INVALID_PERMISSIONS(1004, "token khong co quyen"),

    VALIDATION_FAILED(9000, "validation.failed"),
    INVALID_REQUEST_DATA(9001, "validation.invalid_request"),

    USER_ROLE_NOT_FOUND(2300, "user_role.not_found"),
    USER_NOT_FOUND(2301,"user.not_found"),
    USER_EXISTED(2302,"user.existed"),
    EMAIL_EXISTED(2303,"email.existed"),
    INVALID_PASSWORD(1101, "auth.invalid_password"),

    ;


    ErrorCode(int code ,String message){
        this.code = code;
        this.message = message;
    }
    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

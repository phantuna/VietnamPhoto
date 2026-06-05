package com.example.backend.exception;

public enum ErrorCode {

    JWT_NOT_CREATED(1000, "jwt chua tao"),
    INVALID_SIGNATURE(1001, "sai chư ky "),
    INVALID_TOKEN(1002, "sai token"),
    MISSING_TOKEN(1003, "khong co token"),
    INVALID_PERMISSIONS(1004, "token khong co quyen"),

    VALIDATION_FAILED(9000, "validation.failed"),
    INVALID_REQUEST_DATA(9001, "validation.invalid_request"),

    USER_ROLE_NOT_FOUND(2300, "Không tìm thấy quyền của người dùng"),
    USER_NOT_FOUND(2301,"Tài khoản hoặc email không tồn tại"),
    USER_EXISTED(2302,"Tài khoản đã tồn tại"),
    EMAIL_EXISTED(2303,"Email đã tồn tại"),
    USER_BANNED(2304, "Tài khoản của bạn đã bị khóa do vi phạm. Vui lòng liên hệ Admin."),
    INVALID_PASSWORD(1101, "Mật khẩu không chính xác. Vui lòng thử lại."),

    PHOTO_LOCATION_MISMATCH(3000, "photo.location.mismatch"),
    BANNED_WORD_EXISTED(4001, "Từ cấm này đã tồn tại trong hệ thống"),
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

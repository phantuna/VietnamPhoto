package com.example.backend.exception;

public enum ErrorCode {

    JWT_NOT_CREATED(1000, "Không thể tạo token xác thực"),
    INVALID_SIGNATURE(1001, "Chữ ký token không hợp lệ"),
    INVALID_TOKEN(1002, "Token không hợp lệ hoặc đã hết hạn"),
    MISSING_TOKEN(1003, "Yêu cầu phải có token xác thực"),
    INVALID_PERMISSIONS(1004, "Bạn không có quyền thực hiện hành động này"),
    EXPIRED_TOKEN(1005, "Mã xác nhận (OTP) hoặc Token đã hết hạn"),
    INVALID_OTP(1006, "Mã OTP không chính xác"),
    UNAUTHENTICATED(1007,"Không co quyền truy cập"),

    USER_NOT_FOUND(2001, "Tài khoản hoặc email không tồn tại"),
    USER_EXISTED(2002, "Tài khoản đã tồn tại trong hệ thống"),
    EMAIL_EXISTED(2003, "Email này đã được sử dụng"),
    USER_BANNED(2004, "Tài khoản của bạn đã bị khóa do vi phạm. Vui lòng liên hệ Admin."),
    INVALID_PASSWORD(2005, "Mật khẩu không chính xác. Vui lòng thử lại."),
    PASSWORD_MISMATCH(2008, "Xác nhận mật khẩu không khớp"),
    USER_ROLE_NOT_FOUND(2006, "Không tìm thấy quyền của người dùng"),
    CANNOT_FOLLOW_YOURSELF(2007, "Bạn không thể tự theo dõi chính mình"),

    POST_NOT_FOUND(3001, "Không tìm thấy bài viết"),
    PHOTO_NOT_FOUND(3002, "Không tìm thấy hình ảnh"),
    PHOTO_LOCATION_MISMATCH(3003, "Vị trí hình ảnh không khớp với bài viết"),
    UNAUTHORIZED_POST_ACTION(3004, "Bạn chỉ có thể chỉnh sửa/xóa bài viết của chính mình"),
    POST_LIMIT_EXCEEDED(3005, "Bạn đã đạt giới hạn an toàn hệ thống (bài/ngày)"),
    PHOTO_UPLOAD_FAILED(3006, "Lỗi khi tải ảnh lên hệ thống"),
    INVALID_IMAGE(3007, "Định dạng ảnh không được hỗ trợ. Hệ thống chỉ nhận ảnh chuẩn JPG, JPEG, PNG, HEIC."),
    IMAGE_BLOCKED(3008, "Ảnh chứa nội dung không phù hợp hoặc vi phạm tiêu chuẩn cộng đồng"),

    LOCATION_NOT_FOUND(4001, "Không tìm thấy địa điểm"),
    LOCATION_ALREADY_EXISTS(4002, "Địa điểm này đã tồn tại"),
    LOCATION_TOO_CLOSE(4003, "Địa điểm này quá gần với một địa điểm đã tồn tại"),
    TAG_EXISTED(4004, "Hashtag này đã tồn tại"),
    INVALID_TAG(4005, "Hashtag không hợp lệ hoặc chứa nội dung không phù hợp"),
    TAG_NOT_FOUND(4006, "Không tìm thấy hashtag"),

    COMMENT_NOT_FOUND(5001, "Không tìm thấy bình luận"),
    UNAUTHORIZED_COMMENT_ACTION(5002, "Bạn không có quyền xóa bình luận này"),
    ALREADY_LIKED(5003, "Bạn đã thích bài viết này rồi"),
    NOT_LIKED_YET(5004, "Bạn chưa thích bài viết này"),

    BANNED_WORD_EXISTED(6001, "Từ cấm này đã tồn tại trong hệ thống"),
    REPORT_NOT_FOUND(6002, "Không tìm thấy báo cáo vi phạm"),
    CONTAIN_BANNED_WORDS(6003, "Nội dung của bạn chứa từ ngữ vi phạm tiêu chuẩn cộng đồng"),
    REPORT_ALREADY_RESOLVED(6004, "Báo cáo này đã được xử lý"),
    CANNOT_REVOKE_OWN_ADMIN(6005, "Bạn không thể tự gỡ quyền ADMIN của chính mình"),
    BANNED_WORD_NOT_FOUND(6006, "Không tìm thấy từ cấm trong hệ thống"),

    VALIDATION_FAILED(9000, "Dữ liệu đầu vào không hợp lệ"),
    INVALID_REQUEST_DATA(9001, "Định dạng yêu cầu không đúng"),
    UNCATEGORIZED_EXCEPTION(9997, "Lỗi hệ thống không xác định, vui lòng liên hệ admin"),
    EXTERNAL_SERVICE_ERROR(9998, "Lỗi từ dịch vụ bên ngoài, vui lòng thử lại sau"),
    INTERNAL_SERVER_ERROR(9999, "Lỗi hệ thống nội bộ, vui lòng thử lại sau");


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

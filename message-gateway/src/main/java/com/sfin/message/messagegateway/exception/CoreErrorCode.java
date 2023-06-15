package com.sfin.message.messagegateway.exception;

import org.springframework.http.HttpStatus;

public enum CoreErrorCode {

    SUCCESS(HttpStatus.OK, "200", "Thành công", "ERROR_CORE_SUCCESS"),
    PERMISSION_DENIED(HttpStatus.OK, "201", "Permission Denied", "ERROR_CORE_PERMISSION_DENIED"),
    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SSHOP-500", "Có lỗi xảy ra, xin vui lòng thử lại sau ít phút", "ERROR_CORE_GENERAL_ERROR"),
    ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND,"SSHOP-404","Thực thể không tồn tại", "ERROR_CORE_ENTITY_NOT_EXISTS"),
    ENTITY_EXISTED(HttpStatus.BAD_REQUEST,"SSHOP-409","Thực thể đã tồn tại", "ERROR_CORE_ENTITY_EXISTED"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"SSHOP-402","Truyền sai tham số", "ERROR_CORE_BAD_REQUEST"),
    INVALID_SHOPID(HttpStatus.BAD_REQUEST,"SSHOP-403","ShopId isn't config in pod service", "ERROR_CORE_INVALID_SHOPID"),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SSHOP-405", "Mã truy cập hết hạn", "ERROR_CORE_TOKEN_EXPIRED"),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "SSHOP-406", "Không tìm thấy mã truy cập", "ERROR_CORE_TOKEN_NOT_FOUND"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "SSHOP-407", "Mã truy cập không hợp lệ", "ERROR_CORE_TOKEN_INVALID"),
    CANNOT_SEND_MESSAGE(HttpStatus.BAD_REQUEST,"SSHOP-408","Không thể gửi thông báo", "ERROR_CORE_CANNOT_SEND_MESSAGE"),
    UNAUTHORIZED(HttpStatus.BAD_REQUEST, "SSHOP-409", "Thông tin xác thực bị thiếu hoặc không chính xác", "ERROR_CORE_UNAUTHORIZED"),
    INVALID_USER_PASS(HttpStatus.UNAUTHORIZED, "SSHOP-401","Tài khoản hoặc mật khẩu không đúng, quý khách vui lòng kiểm tra và đăng nhập lại", "ERROR_CORE_INVALID_USER_PASS"),
    PHONE_INVALID(HttpStatus.BAD_REQUEST,"SSHOP-410","Username không đúng", "ERROR_CORE_PHONE_INVALID"),
    USER_NOT_OWNER_DEVICE(HttpStatus.BAD_REQUEST,"SSHOP-411","Tài khoản không khớp với thiết bị", "ERROR_CORE_USER_NOT_OWNER_DEVICE"),
    BARCODE_EXISTED(HttpStatus.BAD_REQUEST,"SSHOP-400","Barcode đã tồn tại", "ERROR_CORE_BARCODE_EXISTED"),
    INVALID_MOBILE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST,"SSHOP-412","Số điện thoại không hợp lệ", "ERROR_CORE_INVALID_MOBILE_NUMBER_FORMAT"),
    DATE_NOT_VALID(HttpStatus.BAD_REQUEST,"SSHOP-414","Thời gian truyền vào không hợp lệ", "ERROR_CORE_DATE_NOT_VALID"),

    MISS_PARAM(HttpStatus.BAD_REQUEST, "SSHOP-415", "Truyền lên thiếu tham số", "ERROR_CORE_MISS_PARAM"),

    BINDING_ALREADY_USED(HttpStatus.BAD_REQUEST, "SSHOP-416", "Port đã được sử dụng", "ERROR_CORE_BINDING");


    private final HttpStatus status;
    private String code;
    private String message;
    private String label;

    CoreErrorCode(HttpStatus status, String code, String message, String label) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String label() { return label;}
}

package com.example.identity.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    USER_EXISTED(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_PASSWORD(1003, "Password must be at least 6 characters"),
    UNAUTHENTICATED(1004, "Unauthenticated"),
    INVALID_KEY(1005, "Invalid message key"),
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

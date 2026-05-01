package com.example.post.utils.exceptions;

import lombok.Getter;

@Getter
public enum NotificationError {
    
    NOTIFICATION_NOT_FOUND(404, "Notification not found"),
    USER_ID_NOT_FOUND(404, "User ID not found");

    private int code;
    private String message;

    NotificationError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

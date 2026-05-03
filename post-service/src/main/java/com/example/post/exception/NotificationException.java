package com.example.post.exception;

import com.example.post.utils.exceptions.NotificationError;

public class NotificationException extends BaseException {

    public NotificationException(NotificationError error) {
        super(error.getCode(), error.getMessage());
    }
}

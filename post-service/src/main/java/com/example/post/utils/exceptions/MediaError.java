package com.example.post.utils.exceptions;

import lombok.Getter;


@Getter
public enum MediaError {
    FILE_IS_EMPTY(1001, "File is empty"),
    COULD_NOT_UPLOAD_FILE(1002, "Could not upload file");

    private int code;
    private String message;

    MediaError(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

package com.example.post.exception;

import com.example.post.utils.exceptions.MediaError;

public class MediaException extends BaseException {

    public MediaException(MediaError error) {
        super(error.getCode(), error.getMessage());
    }
}

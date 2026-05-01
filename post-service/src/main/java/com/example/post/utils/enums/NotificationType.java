package com.example.post.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    LIKE("LIKE"),
    COMMENT("COMMENT"),
    FOLLOW("FOLLOW");

    private final String value;
}

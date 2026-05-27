package com.example.post.dto.Post;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostResponse {

    private String id;

    private String userId;

    private String username;

    private String content;

    private List<String> mediaUrls;

    private Integer likeCount;

    private Integer replyCount;

    private LocalDateTime createdAt;
}

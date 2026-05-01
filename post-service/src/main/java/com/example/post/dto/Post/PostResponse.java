package com.example.post.dto.Post;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {

    private String id;

    private String userId;

    private String username;

    private String content;

    private String mediaUrl;

    private Integer likeCount;

    private Integer replyCount;
    
    private LocalDateTime createdAt;
}

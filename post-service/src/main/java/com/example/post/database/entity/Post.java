package com.example.post.database.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "media_url")
    private String mediaUrl;
    
    private String status; // e.g., PUBLIC, PRIVATE
    
    @Column(name = "like_count")
    @Builder.Default
    private int likeCount = 0;
    
    @Column(name = "reply_count")
    @Builder.Default
    private int replyCount = 0;
    
    @Version
    private Long version;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        createdAt = LocalDateTime.now();
    }
}

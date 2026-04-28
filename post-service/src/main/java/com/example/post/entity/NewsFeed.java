package com.example.post.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "newsfeed")
public class NewsFeed {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "owner_user_id")
    private String ownerUserId; // The user who sees this feed

    @Column(name = "post_id")
    private String postId; // The post they are seeing

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public NewsFeed() {
        this.createdAt = LocalDateTime.now();
    }

    public NewsFeed(String ownerUserId, String postId) {
        this.ownerUserId = ownerUserId;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

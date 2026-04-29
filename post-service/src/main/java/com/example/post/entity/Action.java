package com.example.post.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "actions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "post_id", "action_type" })
})
public class Action {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "action_type")
    private String actionType; // LIKE, COMMENT

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Action() {
        this.createdAt = LocalDateTime.now();
    }

    public Action(String userId, String postId, String actionType) {
        this.userId = userId;
        this.postId = postId;
        this.actionType = actionType;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

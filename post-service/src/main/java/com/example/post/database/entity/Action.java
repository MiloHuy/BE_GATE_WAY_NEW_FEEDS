package com.example.post.database.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "user_id",
        "post_id",
        "action_type"
    })
})
@Data
@Builder
@NoArgsConstructor

@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "action_type")
    private String actionType;

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

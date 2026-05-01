package com.example.post.database.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.post.utils.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "type_id")
    private String typeId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationType type = NotificationType.LIKE;
    
    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

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

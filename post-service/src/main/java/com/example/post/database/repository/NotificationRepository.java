package com.example.post.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.post.database.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Query("SELECT n FROM Notification n "
            + "WHERE n.recipientId = :userId "
            + "ORDER BY n.createdAt DESC")
    List<Notification> getListNotifications(String userId);
}

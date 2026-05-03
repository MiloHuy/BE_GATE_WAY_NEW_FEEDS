package com.example.post.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.post.database.entity.Notification;
import com.example.post.database.repository.NotificationRepository;
import com.example.post.exception.NotificationException;
import com.example.post.utils.enums.NotificationType;
import com.example.post.utils.exceptions.NotificationError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    @Async
    public void createNotification(
        String recipientId, 
        String typeId, 
        NotificationType type, 
        String actorId
    ) {
        // 1. create notification
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .typeId(typeId)
                .type(type)
                .actorId(actorId)
                .build();
        
        // 2. save notification
        notificationRepository.save(notification);
        
        // 3. send notification
        sseService.sendNotification(recipientId, notification);
    }

    /**
     * Get all notifications for a user
     * 
     * @param userId
     * @return list of notifications
     */
    public List<Notification> getNotifications(String userId) {
        // 1. check userID
        if (userId == null || userId.isEmpty()) {
            throw new NotificationException(
                    NotificationError.USER_ID_NOT_FOUND);
        }

        // 2. get all notification
        List<Notification> notifications = notificationRepository.getListNotifications(userId);

        // 3. return data
        return notifications;
    }

    /**
     * Mark notification as read
     * 
     * @param id
     * @return true if success, false if failed
     */
    public Boolean markAsRead(String id) {
        // 1. check id
        if (id == null || id.isEmpty()) {
            throw new NotificationException(
                    NotificationError.NOTIFICATION_NOT_FOUND);
        }

        try {
            // 2. find by id
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new NotificationException(
                            NotificationError.NOTIFICATION_NOT_FOUND));

            // 3. set read to true
            notification.setRead(true);

            // 4. save
            notificationRepository.save(notification);
            log.info("Notification marked as read: " + id);

            return true;

        } catch (Exception e) {

            log.error("Error marking notification as read: ", e);

            return false;
        }
    }
}

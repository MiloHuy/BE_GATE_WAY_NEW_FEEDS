package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import com.example.post.entity.Notification;
import com.example.post.repository.NotificationRepository;
import com.example.post.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public NotificationController(NotificationRepository notificationRepository, SseService sseService) {
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable String userId) {
        return sseService.createEmitter(userId);
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<Notification>> getNotifications(@PathVariable String userId) {
        return ApiResponse.success(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return ApiResponse.success("Marked as read");
    }
}

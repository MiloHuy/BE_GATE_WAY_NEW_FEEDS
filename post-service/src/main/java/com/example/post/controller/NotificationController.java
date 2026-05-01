package com.example.post.controller;

import com.example.post.database.entity.Notification;
import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.service.NotificationService;
import com.example.post.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SseService sseService;
    private final NotificationService notificationService;

    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @PathVariable String userId) {
        return sseService.createEmitter(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AType> getNotifications(
            @PathVariable String userId) {

        List<Notification> notifications = notificationService.getNotifications(userId);

        return ResponseEntity.ok(ApiType.success(notifications));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<AType> markAsRead(
            @PathVariable String id) {

        Boolean isSuccess = notificationService.markAsRead(id);

        return ResponseEntity.ok(ApiType.success(isSuccess));
    }
}

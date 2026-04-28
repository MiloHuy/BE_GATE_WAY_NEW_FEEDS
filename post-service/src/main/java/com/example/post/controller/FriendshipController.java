package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import com.example.post.entity.Friendship;
import com.example.post.entity.Notification;
import com.example.post.repository.FriendshipRepository;
import com.example.post.repository.NotificationRepository;
import com.example.post.service.SseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipRepository friendshipRepository;
    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public FriendshipController(FriendshipRepository friendshipRepository,
                                NotificationRepository notificationRepository,
                                SseService sseService) {
        this.friendshipRepository = friendshipRepository;
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @PostMapping("/follow")
    public ApiResponse<Friendship> follow(@RequestBody Friendship friendship) {
        Friendship saved = friendshipRepository.save(friendship);
        
        Notification noti = new Notification(
            friendship.getFollowingId(), friendship.getFollowerId(), null, "FOLLOW"
        );
        notificationRepository.save(noti);
        
        // Real-time Push via SSE
        sseService.sendNotification(friendship.getFollowingId(), noti);
        
        return ApiResponse.success(saved);
    }
}

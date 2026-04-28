package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import com.example.post.entity.Action;
import com.example.post.entity.Notification;
import com.example.post.entity.Post;
import com.example.post.repository.ActionRepository;
import com.example.post.repository.NotificationRepository;
import com.example.post.repository.PostRepository;
import com.example.post.service.SseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RestController
@RequestMapping("/api/actions")
public class ActionController {

    private final ActionRepository actionRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public ActionController(ActionRepository actionRepository, 
                            PostRepository postRepository,
                            NotificationRepository notificationRepository,
                            SseService sseService) {
        this.actionRepository = actionRepository;
        this.postRepository = postRepository;
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @PostMapping("/like")
    @Transactional
    public ApiResponse<String> toggleLike(@RequestBody Action actionRequest) {
        Optional<Action> existingLike = actionRepository.findByUserIdAndPostIdAndActionType(
                actionRequest.getUserId(), actionRequest.getPostId(), "LIKE");

        Post post = postRepository.findById(actionRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (existingLike.isPresent()) {
            actionRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            return ApiResponse.success("Unliked");
        } else {
            Action action = new Action(actionRequest.getUserId(), actionRequest.getPostId(), "LIKE");
            actionRepository.save(action);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            if (!post.getUserId().equals(actionRequest.getUserId())) {
                Notification noti = new Notification(
                    post.getUserId(), actionRequest.getUserId(), post.getId(), "LIKE"
                );
                notificationRepository.save(noti);
                
                // Real-time Push via SSE
                sseService.sendNotification(post.getUserId(), noti);
            }

            return ApiResponse.success("Liked");
        }
    }
}

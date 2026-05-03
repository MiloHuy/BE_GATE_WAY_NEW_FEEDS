package com.example.post.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.post.database.entity.Action;
import com.example.post.database.entity.Post;
import com.example.post.database.repository.ActionRepository;
import com.example.post.database.repository.PostRepository;
import com.example.post.utils.enums.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final ActionRepository actionRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public String toggleLike(Action actionRequest) {
        // 1. Validate input
        if (actionRequest.getUserId() == null || actionRequest.getPostId() == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        String userId = actionRequest.getUserId();
        String postId = actionRequest.getPostId();
        String actionType = "LIKE";

        // 2. Check if user already liked the post
        Optional<Action> existingLike = actionRepository.getAction(
                userId, postId, actionType);

        // 3. Get post and check if post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 4. If user already liked the post, unlike it
        if (existingLike.isPresent()) {

            actionRepository.delete(existingLike.get());

            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));

            postRepository.save(post);

            return "Unliked";

        }
        // 5. Like the post and create notification if the user is not the post owner
        else {
            // save action
            Action action = Action.builder()
                    .userId(userId)
                    .postId(postId)
                    .actionType(actionType)
                    .build();

            actionRepository.save(action);

            post.setLikeCount(post.getLikeCount() + 1);

            postRepository.save(post);

            // Create notification if the user is not the post owner
            if (!post.getUserId().equals(userId)) {

                notificationService.createNotification(
                        post.getUserId(),
                        postId,
                        NotificationType.LIKE,
                        userId);
            }

            return "Liked";
        }
    }
    
}

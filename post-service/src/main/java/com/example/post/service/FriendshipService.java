package com.example.post.service;

import org.springframework.stereotype.Service;

import com.example.post.database.entity.Friendship;
import com.example.post.database.repository.FriendshipRepository;
import com.example.post.utils.enums.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;

    public String followToggle(Friendship friendship) {

        String followerID = friendship.getFollowerId();
        String followingID = friendship.getFollowingId();

        // check if user is already followed
        Friendship friendshipFound = friendshipRepository.checkUserFollowed(followerID, followingID);

        // if user is already followed, delete it
        if (friendshipFound != null) {
            friendshipRepository.delete(friendshipFound);
            return "Unfollowed";
        }

        // save friendship
        friendshipRepository.save(friendship);

        // send notification
        notificationService.createNotification(
            friendship.getFollowingId(), 
            friendship.getFollowerId(), 
            NotificationType.FOLLOW, 
            friendship.getFollowerId());

        return "Followed";
    }
    
}

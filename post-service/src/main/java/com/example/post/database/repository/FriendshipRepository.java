package com.example.post.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.post.database.entity.Friendship;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    @Query("SELECT f FROM Friendship f "
            + "WHERE f.followingId = :followingId ")
    List<Friendship> getFollowingList(String followingId);

    @Query("SELECT f FROM Friendship f "
            + "WHERE f.followerId = :followerId ")
    List<Friendship> getFollowerList(String followerId);
    
    @Query("SELECT f FROM Friendship f "
            + "WHERE f.followerId = :followerId AND f.followingId = :followingId ")
    Friendship checkUserFollowed(String followerId, String followingId);

    @Query("SELECT f FROM Friendship f "
            + "WHERE f.followerId = :userId1 AND f.followingId = :userId2 OR f.followerId = :userId2 AND f.followingId = :userId1")
    Friendship findByUserIds(String userId1, String userId2);

 
}

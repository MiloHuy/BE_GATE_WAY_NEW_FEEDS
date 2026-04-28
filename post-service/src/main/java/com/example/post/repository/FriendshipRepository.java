package com.example.post.repository;

import com.example.post.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    List<Friendship> findByFollowingId(String followingId);
    List<Friendship> findByFollowerId(String followerId);
}

package com.example.post.repository;

import com.example.post.entity.NewsFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsFeedRepository extends JpaRepository<NewsFeed, String> {
    List<NewsFeed> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
}

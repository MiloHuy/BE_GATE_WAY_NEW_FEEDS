package com.example.post.repository;

import com.example.post.entity.NewsFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsFeedRepository extends JpaRepository<NewsFeed, String> {
    Page<NewsFeed> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId, Pageable pageable);
}

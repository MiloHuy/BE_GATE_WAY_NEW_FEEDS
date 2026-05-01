package com.example.post.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.example.post.database.entity.NewsFeed;

public interface NewsFeedRepository extends JpaRepository<NewsFeed, String> {
    @Query("SELECT nf FROM NewsFeed nf "
            + "WHERE nf.ownerUserId = :userId "
            + "ORDER BY nf.createdAt DESC")
    Page<NewsFeed> getNewsFeed(String userId, Pageable pageable);
}

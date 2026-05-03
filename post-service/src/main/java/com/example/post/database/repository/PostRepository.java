package com.example.post.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;

import com.example.post.database.entity.Post;

public interface PostRepository extends JpaRepository<Post, String> {
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> getLatestPosts(Pageable pageable);
}

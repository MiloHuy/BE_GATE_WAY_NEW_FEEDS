package com.example.post.controller;

import com.example.post.database.entity.Post;
import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<AType> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiType.success(postService.getAllPosts(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AType> getPostById(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiType.success(postService.getPostById(id)));
    }

    @GetMapping("/feed/{userId}")
    public ResponseEntity<AType> getFeedByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiType.success(postService.getFeedForUser(userId, page, size)));
    }

    @PostMapping
    public ResponseEntity<AType> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(ApiType.success(postService.createPost(post)));
    }
}

package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import com.example.post.dto.PostResponse;
import com.example.post.entity.Post;
import com.example.post.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> getAllPosts() {
        return ApiResponse.success(postService.getAllPosts());
    }

    @GetMapping("/feed/{userId}")
    public ApiResponse<List<PostResponse>> getFeedByUserId(@PathVariable String userId) {
        return ApiResponse.success(postService.getFeedForUser(userId));
    }

    @PostMapping
    public ApiResponse<Post> createPost(@RequestBody Post post) {
        return ApiResponse.success(postService.createPost(post));
    }
}

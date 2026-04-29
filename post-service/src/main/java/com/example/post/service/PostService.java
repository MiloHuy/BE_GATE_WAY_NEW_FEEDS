package com.example.post.service;

import com.example.post.client.UserClient;
import com.example.post.dto.PostResponse;
import com.example.post.entity.Friendship;
import com.example.post.entity.NewsFeed;
import com.example.post.entity.Post;
import com.example.post.repository.FriendshipRepository;
import com.example.post.repository.NewsFeedRepository;
import com.example.post.repository.PostRepository;
import com.example.proto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final NewsFeedRepository newsFeedRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserClient userClient;

    public PostService(PostRepository postRepository,
            NewsFeedRepository newsFeedRepository,
            FriendshipRepository friendshipRepository,
            UserClient userClient) {
        this.postRepository = postRepository;
        this.newsFeedRepository = newsFeedRepository;
        this.friendshipRepository = friendshipRepository;
        this.userClient = userClient;
    }

    @Transactional
    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);
        List<Friendship> followers = friendshipRepository.findByFollowingId(post.getUserId());

        List<NewsFeed> feedItems = followers.stream()
                .map(f -> new NewsFeed(f.getFollowerId(), savedPost.getId()))
                .collect(Collectors.toList());

        feedItems.add(new NewsFeed(post.getUserId(), savedPost.getId()));
        newsFeedRepository.saveAll(feedItems);
        return savedPost;
    }

    public PostResponse getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post);
    }

    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    public Page<PostResponse> getFeedForUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsFeed> feedItems = newsFeedRepository.findByOwnerUserIdOrderByCreatedAtDesc(userId, pageable);
        List<String> postIds = feedItems.stream().map(NewsFeed::getPostId).collect(Collectors.toList());

        List<PostResponse> posts = postRepository.findAllById(postIds).stream()
                .map(this::mapToResponse)
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Ensure order
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(posts, pageable, feedItems.getTotalElements());
    }

    private PostResponse mapToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setContent(post.getContent());
        response.setMediaUrl(post.getMediaUrl());
        response.setLikeCount(post.getLikeCount());
        response.setReplyCount(post.getReplyCount());
        response.setCreatedAt(post.getCreatedAt());

        UserResponse user = userClient.getUser(post.getUserId());
        response.setUsername(user.getUsername());

        return response;
    }
}

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

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getFeedForUser(String userId) {
        List<NewsFeed> feedItems = newsFeedRepository.findByOwnerUserIdOrderByCreatedAtDesc(userId);
        List<String> postIds = feedItems.stream().map(NewsFeed::getPostId).collect(Collectors.toList());
        return postRepository.findAllById(postIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

        // Call Identity Service via gRPC
        UserResponse user = userClient.getUser(post.getUserId());
        response.setUsername(user.getUsername());
        
        return response;
    }
}

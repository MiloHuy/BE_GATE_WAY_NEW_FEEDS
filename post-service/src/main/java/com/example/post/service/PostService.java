package com.example.post.service;

import com.example.post.client.UserClient;
import com.example.post.database.entity.Friendship;
import com.example.post.database.entity.NewsFeed;
import com.example.post.database.entity.Post;
import com.example.post.database.repository.FriendshipRepository;
import com.example.post.database.repository.NewsFeedRepository;
import com.example.post.database.repository.PostRepository;
import com.example.post.dto.Post.PostResponse;
import com.example.proto.UserResponse;
import org.springframework.data.domain.PageImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final NewsFeedRepository newsFeedRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserClient userClient;

    @Transactional
    public Post createPost(Post post) {

        Post savedPost = postRepository.save(post);

        List<Friendship> followers = friendshipRepository.getFollowingList(post.getUserId());

        List<NewsFeed> feedItems = followers.stream()
                .map(f -> NewsFeed.builder()
                        .ownerUserId(f.getFollowerId())
                        .postId(savedPost.getId())
                        .build())
                .collect(Collectors.toList());

        feedItems.add(NewsFeed.builder()
                .ownerUserId(post.getUserId())
                .postId(savedPost.getId())
                .build());

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
        return postRepository.getLatestPosts(pageable)
                .map(this::mapToResponse);
    }

    public Page<PostResponse> getFeedForUser(String userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        
        Page<NewsFeed> feedItems = newsFeedRepository.getNewsFeed(userId, pageable);
        
        List<String> postIds = feedItems.stream()
                .map(NewsFeed::getPostId)
                .collect(Collectors.toList());

        List<PostResponse> posts = postRepository.findAllById(postIds).stream()
                .map(this::mapToResponse)
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());

        return new PageImpl<>(posts, pageable, feedItems.getTotalElements());
    }

    private PostResponse mapToResponse(Post post) {
        
        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .likeCount(post.getLikeCount())
                .replyCount(post.getReplyCount())
                .createdAt(post.getCreatedAt())
                .build();

        UserResponse user = userClient.getUser(post.getUserId());
        response.setUsername(user.getUsername());

        return response;
    }
}

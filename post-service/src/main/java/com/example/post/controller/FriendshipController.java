package com.example.post.controller;

import com.example.post.database.entity.Friendship;
import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.service.FriendshipService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/follow")
    public ResponseEntity<AType> follow(@RequestBody Friendship friendship) {

        String result = friendshipService.followToggle(friendship);

        return ResponseEntity.ok(ApiType.success(result));
    }
}

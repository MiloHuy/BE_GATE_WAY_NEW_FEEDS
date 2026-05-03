package com.example.post.controller;

import com.example.post.database.entity.Action;
import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.service.ActionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;


    @PostMapping("/like")
    @Transactional
    public ResponseEntity<AType> toggleLike(@RequestBody Action actionRequest) {
        String result = actionService.toggleLike(actionRequest);
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Success")
                .data(result)
                .build());
    }
    
}

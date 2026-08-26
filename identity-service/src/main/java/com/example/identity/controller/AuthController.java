package com.example.identity.controller;

import com.example.identity.dto.API.AType;
import com.example.identity.dto.req.AuthRequest;
import com.example.identity.dto.req.RefreshRequest;
import com.example.identity.dto.req.RegisterRequest;
import com.example.identity.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AType login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AType register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/refresh")
    public AType refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }
}

package com.example.identity.controller;

import com.example.identity.dto.*;
import com.example.identity.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }
}

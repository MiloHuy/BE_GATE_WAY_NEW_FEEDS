package com.example.src.controller;

import com.example.src.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final JwtService jwtService;

    public LogoutController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        return jwtService.addToBlacklist(token)
                .map(success -> {
                    if (success) {
                        return ResponseEntity.ok("Successfully logged out and token blacklisted");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is invalid or already expired");
                    }
                });
    }
}

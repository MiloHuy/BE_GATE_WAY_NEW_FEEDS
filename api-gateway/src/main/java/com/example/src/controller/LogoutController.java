package com.example.src.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.src.dto.req.LogoutRequest;
import com.example.src.services.RedisService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final RedisService redisService;

    @Value("${jwt.refresh-expiration:604800000}")
    private final long refreshExpiration = 604800000L;

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(
            @AuthenticationPrincipal Jwt accessToken,
            @RequestBody(required = false) LogoutRequest request) {
        Mono<Long> deleteAccessToken = redisService.deleteToken(accessToken.getTokenValue());
        Mono<Boolean> blacklistAccessToken = redisService.blacklistToken(
                accessToken.getTokenValue(), ttlUntil(accessToken.getExpiresAt()));
        Mono<Void> revokeRefreshToken = revokeRefreshTokenIfOwnedByUser(request, accessToken.getSubject());

        return Mono.when(deleteAccessToken, blacklistAccessToken, revokeRefreshToken)
                .thenReturn(ResponseEntity.ok("logout success"));
    }

    private Mono<Void> revokeRefreshTokenIfOwnedByUser(LogoutRequest request, String userId) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            return Mono.empty();
        }

        String refreshToken = request.refreshToken();
        return redisService.getToken(refreshToken)
                .filter(userId::equals)
                .flatMap(ignored -> Mono.when(
                        redisService.deleteToken(refreshToken),
                        redisService.blacklistToken(refreshToken, Duration.ofMillis(refreshExpiration))))
                .then();
    }

    private Duration ttlUntil(Instant expiresAt) {
        if (expiresAt == null) {
            return Duration.ZERO;
        }
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        return ttl.isNegative() ? Duration.ZERO : ttl;
    }
}

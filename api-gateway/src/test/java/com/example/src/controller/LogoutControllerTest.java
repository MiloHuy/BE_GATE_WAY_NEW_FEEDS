package com.example.src.controller;

import com.example.src.dto.req.LogoutRequest;
import com.example.src.services.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogoutControllerTest {

    private static final String USER_ID = "user-1";
    private static final String ACCESS_TOKEN = "access-token";

    @Test
    void logoutDeletesAccessAndOwnedRefreshToken() {
        InMemoryRedisService redisService = new InMemoryRedisService();
        redisService.put(ACCESS_TOKEN, USER_ID);
        redisService.put("refresh-token", USER_ID);
        LogoutController logoutController = new LogoutController(redisService);
        Jwt jwt = accessToken();
        LogoutRequest request = new LogoutRequest("refresh-token");

        ResponseEntity<String> response = logoutController.logout(jwt, request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("logout success", response.getBody());
        assertFalse(redisService.contains(ACCESS_TOKEN));
        assertFalse(redisService.contains("refresh-token"));
        assertTrue(redisService.isBlacklisted(ACCESS_TOKEN));
        assertTrue(redisService.isBlacklisted("refresh-token"));
    }

    @Test
    void logoutWithoutRefreshTokenOnlyDeletesAccessToken() {
        InMemoryRedisService redisService = new InMemoryRedisService();
        redisService.put(ACCESS_TOKEN, USER_ID);
        redisService.put("refresh-token", USER_ID);
        LogoutController logoutController = new LogoutController(redisService);
        Jwt jwt = accessToken();

        ResponseEntity<String> response = logoutController.logout(jwt, null).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(redisService.contains(ACCESS_TOKEN));
        assertTrue(redisService.contains("refresh-token"));
        assertTrue(redisService.isBlacklisted(ACCESS_TOKEN));
        assertFalse(redisService.isBlacklisted("refresh-token"));
    }

    @Test
    void logoutDoesNotDeleteRefreshTokenOwnedByAnotherUser() {
        InMemoryRedisService redisService = new InMemoryRedisService();
        redisService.put(ACCESS_TOKEN, USER_ID);
        redisService.put("another-refresh-token", "user-2");
        LogoutController logoutController = new LogoutController(redisService);
        Jwt jwt = accessToken();
        LogoutRequest request = new LogoutRequest("another-refresh-token");

        ResponseEntity<String> response = logoutController.logout(jwt, request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(redisService.contains(ACCESS_TOKEN));
        assertTrue(redisService.contains("another-refresh-token"));
        assertTrue(redisService.isBlacklisted(ACCESS_TOKEN));
        assertFalse(redisService.isBlacklisted("another-refresh-token"));
    }

    private Jwt accessToken() {
        Instant now = Instant.now();
        return Jwt.withTokenValue(ACCESS_TOKEN)
                .header("alg", "HS256")
                .subject(USER_ID)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .claim("role", "ROLE_USER")
                .claim("type", "ACCESS")
                .build();
    }

    private static class InMemoryRedisService extends RedisService {

        private final Map<String, String> tokens = new HashMap<>();
        private final Map<String, String> blacklistedTokens = new HashMap<>();

        InMemoryRedisService() {
            super(null);
        }

        void put(String token, String userId) {
            tokens.put(token, userId);
        }

        boolean contains(String token) {
            return tokens.containsKey(token);
        }

        boolean isBlacklisted(String token) {
            return blacklistedTokens.containsKey(token);
        }

        @Override
        public Mono<String> getToken(String token) {
            return Mono.justOrEmpty(tokens.get(token));
        }

        @Override
        public Mono<Long> deleteToken(String token) {
            return Mono.just(tokens.remove(token) == null ? 0L : 1L);
        }

        @Override
        public Mono<Boolean> blacklistToken(String token, java.time.Duration ttl) {
            if (ttl == null || ttl.isZero() || ttl.isNegative()) {
                return Mono.just(false);
            }
            blacklistedTokens.put(token, "revoked");
            return Mono.just(true);
        }
    }
}

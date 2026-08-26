package com.example.src.services;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> saveToken(String token, String userId) {
        return redisTemplate.opsForValue().set(token, userId);
    }

    public Mono<Boolean> hasToken(String token) {
        return redisTemplate.hasKey(token);
    }

    public Mono<String> getToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public Mono<Long> deleteToken(String token) {
        return redisTemplate.delete(token);
    }

    public Mono<Boolean> blacklistToken(String token, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return Mono.just(false);
        }
        return redisTemplate.opsForValue().set(blacklistKey(token), "revoked", ttl);
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(blacklistKey(token));
    }

    private String blacklistKey(String token) {
        return BLACKLIST_PREFIX + token;
    }
}

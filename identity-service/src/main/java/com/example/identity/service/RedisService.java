package com.example.identity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValueWithExpiry(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(jwtExpiration));
    }

    public void setRefreshTokenWithExpiry(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(refreshExpiration));
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void removeValue(String key) {
        redisTemplate.delete(key);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}

package com.example.identity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValueWithExpiry(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(jwtExpiration));
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void removeValue(String key) {
        redisTemplate.delete(key);
    }
}

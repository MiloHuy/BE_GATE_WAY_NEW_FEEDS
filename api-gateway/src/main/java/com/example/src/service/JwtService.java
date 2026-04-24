package com.example.src.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final ReactiveStringRedisTemplate redisTemplate;

    public JwtService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // --- Redis Logic ---

    private long getRemainingTimeInMillis(String token) {
        try {
            Claims claims = extractAllClaims(token);
            long expirationTime = claims.getExpiration().getTime();
            long remainingTime = expirationTime - System.currentTimeMillis();
            return Math.max(0, remainingTime);
        } catch (Exception e) {
            return 0;
        }
    }

    public Mono<Boolean> isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token)
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> isWhitelisted(String token) {
        return redisTemplate.hasKey("whitelist:" + token)
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> addToWhitelist(String token) {
        long remainingTime = getRemainingTimeInMillis(token);
        if (remainingTime <= 0) return Mono.just(false);

        return redisTemplate.opsForValue()
                .set("whitelist:" + token, "true", Duration.ofMillis(remainingTime));
    }

    public Mono<Boolean> addToBlacklist(String token) {
        long remainingTime = getRemainingTimeInMillis(token);
        if (remainingTime <= 0) return Mono.just(false);

        // Add to blacklist and optionally delete from whitelist in sequence
        return redisTemplate.opsForValue()
                .set("blacklist:" + token, "true", Duration.ofMillis(remainingTime))
                .then(redisTemplate.delete("whitelist:" + token))
                .thenReturn(true);
    }
}

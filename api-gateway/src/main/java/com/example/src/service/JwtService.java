package com.example.src.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.security.MessageDigest;
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

    public Mono<Boolean> isTokenValid(String token) {
        Claims claims;
        try {
            claims = extractAllClaims(token);
        } catch (Exception e) {
            return Mono.just(false);
        }

        if (claims.getExpiration().before(new Date())) {
            return Mono.just(false);
        }

        return isBlacklisted(token)
                .flatMap(isBlacklisted -> isBlacklisted
                        ? Mono.just(false)
                        : isWhitelisted(token));
    }

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

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }

    public Mono<Boolean> isBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + hashToken(token))
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> isWhitelisted(String token) {
        return redisTemplate.hasKey("whitelist:" + hashToken(token))
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> addToWhitelist(String token) {
        long remainingTime = getRemainingTimeInMillis(token);
        if (remainingTime <= 0)
            return Mono.just(false);

        return redisTemplate.opsForValue()
                .set("whitelist:" + token, "true", Duration.ofMillis(remainingTime));
    }

    public Mono<Boolean> addToBlacklist(String token) {
        long remainingTime = getRemainingTimeInMillis(token);
        if (remainingTime <= 0)
            return Mono.just(false);

        String key = "blacklist: " + hashToken(token);
        return redisTemplate.opsForValue()
                .set(key, "true", Duration.ofMillis(remainingTime))
                .flatMap(success -> {
                    if (success) {
                        return redisTemplate.delete("whitelist: " + hashToken(token))
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }
}

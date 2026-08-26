package com.example.src.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.src.services.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    Jwt jwt = auth.getToken();
                    String tokenValue = jwt.getTokenValue();
                    return redisService.isTokenBlacklisted(tokenValue)
                            .flatMap(isBlacklisted -> {
                                if (Boolean.TRUE.equals(isBlacklisted)) {
                                    return Mono.error(new ResponseStatusException(
                                            HttpStatus.UNAUTHORIZED, "Token invalidated"));
                                }
                                return redisService.hasToken(tokenValue)
                                        .flatMap(hasKey -> {
                                            if (Boolean.TRUE.equals(hasKey)) {
                                                String userId = jwt.getSubject();
                                                String role = jwt.getClaimAsString("role");

                                                ServerWebExchange mutatedExchange = exchange.mutate()
                                                        .request(r -> r.headers(headers -> {
                                                            headers.set("X-User-Id", userId);
                                                            headers.set("X-User-Role", role);
                                                        }))
                                                        .build();
                                                return chain.filter(mutatedExchange);
                                            }
                                            return Mono.error(new ResponseStatusException(
                                                    HttpStatus.UNAUTHORIZED, "Token invalidated"));
                                        });
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

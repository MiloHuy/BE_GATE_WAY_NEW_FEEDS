package com.example.src.security;

import com.example.src.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtService jwtService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/gateway");

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT check for public paths
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        return jwtService.isBlacklisted(token)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        return unauthorized(exchange);
                    }

                    return jwtService.isWhitelisted(token)
                            .flatMap(isWhitelisted -> {
                                if (isWhitelisted) {
                                    return proceedWithClaims(token, exchange, chain);
                                } else {
                                    return jwtService.isTokenValid(token)
                                            .flatMap(isValid -> {
                                                if (!isValid) {
                                                    return unauthorized(exchange);
                                                }

                                                return jwtService.addToWhitelist(token)
                                                        .then(proceedWithClaims(token, exchange, chain));
                                            });
                                }
                            });
                });
    }

    private Mono<Void> proceedWithClaims(String token, ServerWebExchange exchange, WebFilterChain chain) {
        try {
            Claims claims = jwtService.extractAllClaims(token);
            String username = claims.getSubject();

            List<String> roles = claims.get("roles", List.class);
            List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                    authorities);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(r -> r.header("X-User-Id", username)
                            .header("X-User-Roles", roles == null ? "" : String.join(",", roles)))
                    .build();

            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }
}

package com.example.src.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Request: " + exchange.getRequest().getURI());
        log.info("Headers: " + exchange.getRequest().getHeaders());
        return chain.filter(exchange).doOnSuccess(v -> {
            log.info("Response: " + exchange.getResponse().getStatusCode());
        });
    }

}
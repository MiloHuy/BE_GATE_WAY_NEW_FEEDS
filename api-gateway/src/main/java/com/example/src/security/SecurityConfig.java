package com.example.src.security;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.core.convert.converter.Converter;

import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        private final String[] WHITE_LIST_URLS = {
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh",
                        "/api/auth/validate",
                        "/api/media/**",
                        "/api/posts/**",
                        "/api/gateway/**"
        };

        @Value("${JWT_SECRET}")
        private String secretKey;

        @Value("${JWT_ALGO}")
        private String algorithm;

        @Bean
        ReactiveJwtDecoder jwtDecoder() {
                SecretKey key = new SecretKeySpec(secretKey.getBytes(), algorithm);
                return NimbusReactiveJwtDecoder.withSecretKey(key).build();
        }

        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
                return http
                        .csrf(ServerHttpSecurity.CsrfSpec::disable)
                        .authorizeExchange(exchanges -> exchanges
                                        .pathMatchers(WHITE_LIST_URLS).permitAll()
                                        .anyExchange().authenticated())
                        .oauth2ResourceServer(oauth2 -> oauth2
                                        .jwt(jwt -> jwt
                                                        .jwtDecoder(jwtDecoder())
                                                                .jwtAuthenticationConverter(
                                                                        jwtAuthenticationConverter())))
                        .build();
        }

        @Bean
        Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter authoritiesConverter =
                                new JwtGrantedAuthoritiesConverter();

                authoritiesConverter.setAuthoritiesClaimName("role");

                JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

                jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

                return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
        }

}
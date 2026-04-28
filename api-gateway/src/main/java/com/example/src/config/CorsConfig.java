package com.example.src.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow your frontend origins — update these as needed
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200"));

        // Allow all common HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers (Authorization, Content-Type, etc.)
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (needed if you use JWT in cookies or Authorization header)
        config.setAllowCredentials(true);

        // How long the browser caches the preflight response (in seconds)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all routes

        return new CorsWebFilter(source);
    }
}

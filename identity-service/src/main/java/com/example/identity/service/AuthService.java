package com.example.identity.service;

import com.example.identity.dto.AuthRequest;
import com.example.identity.dto.AuthResponse;
import com.example.identity.dto.RegisterRequest;
import com.example.identity.entity.User;
import com.example.identity.repository.UserRepository;
import com.example.identity.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = jwtProvider.generateToken(user.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String accessToken = jwtProvider.generateToken(user.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtProvider.extractUsername(refreshToken);

        // Ensure user still exists
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtProvider.generateToken(user.getUsername());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}

package com.example.identity.service;

import com.example.identity.database.entity.User;
import com.example.identity.database.repository.UserRepository;
import com.example.identity.dto.API.AType;
import com.example.identity.dto.API.ApiType;
import com.example.identity.dto.API.ErrorType;
import com.example.identity.dto.req.AuthRequest;
import com.example.identity.dto.req.RegisterRequest;
import com.example.identity.dto.res.AuthResponse;
import com.example.identity.exception.AppException;
import com.example.identity.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
            RedisService redisService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    public AType login(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorType.badRequest("Invalid username or password")));

        validateActiveUser(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorType.badRequest("Invalid username or password"));
        }

        // subject = userId (UUID) so the gateway can forward X-User-Id that resolves
        // via findById()
        String accessToken = jwtProvider.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisService.setValueWithExpiry(accessToken, user.getId());
        redisService.setRefreshTokenWithExpiry(refreshToken, user.getId());

        return ApiType.success(new AuthResponse(accessToken, refreshToken));
    }

    public AType register(RegisterRequest request) {
        // 1 check user duplicate username or email

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorType.badRequest("Username already exists"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorType.badRequest("Email already exists"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.save(user);

        String accessToken = jwtProvider.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisService.setValueWithExpiry(accessToken, user.getId());
        redisService.setRefreshTokenWithExpiry(refreshToken, user.getId());

        return ApiType.success(new AuthResponse(accessToken, refreshToken));
    }

    public AType refresh(String refreshToken) {

        if (redisService.isBlacklisted(refreshToken)) {
            throw new AppException(ErrorType.badRequest("Invalid refresh token"));
        }

        if (!jwtProvider.isRefreshTokenValid(refreshToken)) {
            throw new AppException(ErrorType.badRequest("Invalid refresh token"));
        }

        // subject of refreshToken is userId (UUID)
        String userId = jwtProvider.extractUsername(refreshToken);

        String storedUserId = redisService.getValue(refreshToken);
        if (!userId.equals(storedUserId)) {
            throw new AppException(ErrorType.badRequest("Invalid refresh token"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.badRequest("User not found")));

        validateActiveUser(user);

        String newAccessToken = jwtProvider.generateToken(user.getId(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisService.removeValue(refreshToken);
        redisService.setValueWithExpiry(newAccessToken, user.getId());
        redisService.setRefreshTokenWithExpiry(newRefreshToken, user.getId());

        return ApiType.success(new AuthResponse(newAccessToken, newRefreshToken));
    }

    private void validateActiveUser(User user) {
        if (Boolean.FALSE.equals(user.getIsEnable())
                || Boolean.TRUE.equals(user.getIsLocked())
                || Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new AppException(ErrorType.forbidden("User account is not active"));
        }
    }
}

package com.example.identity.service;

import org.springframework.stereotype.Service;

import com.example.identity.database.entity.User;
import com.example.identity.database.repository.UserRepository;
import com.example.identity.dto.API.AType;
import com.example.identity.dto.API.ApiType;
import com.example.identity.dto.API.ErrorType;
import com.example.identity.exception.AppException;
import com.example.identity.dto.req.UpdateProfileRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AType getUserProfile(String userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.notFound("User not found")));

        return ApiType.success(user);
    }

    public AType updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorType.notFound("User not found")));
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        
        userRepository.save(user);
        return ApiType.success(user);
    }
}

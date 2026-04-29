package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import com.example.post.service.S3Service;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final S3Service s3Service;

    public MediaController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ApiResponse<>(1001, "File is empty", "error", null);
        }

        try {
            String fileUrl = s3Service.uploadFile(file);
            return ApiResponse.success(fileUrl);
        } catch (IOException e) {
            return new ApiResponse<>(1002, "Could not upload file: " + e.getMessage(), "error", null);
        }
    }
}

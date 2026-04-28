package com.example.post.controller;

import com.example.post.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ApiResponse<>(1001, "File is empty", "error", null);
        }

        try {
            // Create directory if not exists
            Path root = Paths.get(UPLOAD_DIR);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = root.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), path);

            // In real app, this would be a full URL (e.g., http://localhost:8080/uploads/...)
            return ApiResponse.success("/" + UPLOAD_DIR + filename);

        } catch (IOException e) {
            return new ApiResponse<>(1002, "Could not upload file: " + e.getMessage(), "error", null);
        }
    }
}

package com.example.post.controller;

import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.exception.MediaException;
import com.example.post.service.CloudinaryService;
import com.example.post.utils.exceptions.MediaError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@Slf4j
@RequiredArgsConstructor
public class MediaController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<AType> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("Upload 1 file: {}", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new MediaException(MediaError.FILE_IS_EMPTY);
        }

        try {
            return ResponseEntity.ok(ApiType.success(cloudinaryService.uploadFile(file)));

        } catch (Exception e) {
            log.error("Upload error", e);
            throw new MediaException(MediaError.COULD_NOT_UPLOAD_FILE);
        }
    }
}

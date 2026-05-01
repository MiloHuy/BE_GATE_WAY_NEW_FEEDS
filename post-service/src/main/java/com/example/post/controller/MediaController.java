package com.example.post.controller;

import com.example.post.dto.API.AType;
import com.example.post.dto.API.ApiType;
import com.example.post.exception.MediaException;
import com.example.post.service.S3Service;
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

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<AType> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("Upload file: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new MediaException(MediaError.FILE_IS_EMPTY);
        }

        try {
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok(ApiType.success(fileUrl));

        } catch (Exception e) {
            throw new MediaException(MediaError.COULD_NOT_UPLOAD_FILE);
        }
    }
}

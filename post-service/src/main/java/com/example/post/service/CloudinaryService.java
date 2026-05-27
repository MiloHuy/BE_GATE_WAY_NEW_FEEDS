package com.example.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.post.dto.Media.MediaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public List<MediaResponse> uploadFiles(List<MultipartFile> files) {
        List<MediaResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                responses.add(uploadFile(file));
            } catch (IOException e) {
                log.error("Failed to upload file {}: {}", file.getOriginalFilename(), e.getMessage());
            }
        }
        return responses;
    }

    public MediaResponse uploadFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String type = classifyFileType(contentType);

        String resourceType = toCloudinaryResourceType(type);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "post-media",
                        "resource_type", resourceType));

        String url = (String) result.get("secure_url");

        return MediaResponse.builder()
                .url(url)
                .type(type)
                .build();
    }

    private String classifyFileType(String contentType) {
        if (contentType == null)
            return "OTHER";
        if (contentType.startsWith("image/"))
            return "IMAGE";
        if (contentType.startsWith("video/"))
            return "VIDEO";
        if (contentType.startsWith("audio/"))
            return "AUDIO";
        if (contentType.contains("pdf")
                || contentType.contains("msword")
                || contentType.contains("office"))
            return "DOCUMENT";
        return "OTHER";
    }

    /**
     * Maps our internal type to the Cloudinary resource_type parameter.
     * <ul>
     * <li>IMAGE → "image"</li>
     * <li>VIDEO → "video"</li>
     * <li>AUDIO / DOCUMENT / OTHER → "raw"</li>
     * </ul>
     */
    private String toCloudinaryResourceType(String type) {
        return switch (type) {
            case "IMAGE" -> "image";
            case "VIDEO" -> "video";
            default -> "raw";
        };
    }
}

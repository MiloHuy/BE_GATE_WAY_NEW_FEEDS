package com.example.post.dto.Media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponse {
    private String url;
    private String type; // IMAGE, VIDEO, DOCUMENT, OTHER
}

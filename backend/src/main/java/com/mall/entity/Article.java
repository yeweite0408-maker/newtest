package com.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String category;
    private String tags;
    private Long authorId;
    private String authorName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

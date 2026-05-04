package com.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String toUsername;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

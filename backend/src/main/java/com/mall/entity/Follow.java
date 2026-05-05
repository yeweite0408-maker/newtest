package com.mall.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Follow {
    private Long id;
    private Long followerId;
    private Long followeeId;
    private String followeeName;
    private String followerName;
    private LocalDateTime createdAt;
}

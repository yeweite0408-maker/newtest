package com.mall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String avatar;
    private String bio;
    private String role;
    private LocalDateTime createdAt;
}

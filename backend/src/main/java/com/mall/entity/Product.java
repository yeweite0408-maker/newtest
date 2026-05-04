package com.mall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String category;
    private Integer stock;
    private String developer;
    private LocalDateTime createdAt;
}

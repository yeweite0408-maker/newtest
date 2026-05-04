package com.mall.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
}

package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.CartItem;
import com.mall.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<List<CartItem>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.findByUserId(userId));
    }

    @PostMapping
    public Result<?> add(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = Long.valueOf(params.get("productId").toString());
        int quantity = Integer.parseInt(params.getOrDefault("quantity", "1").toString());
        if (cartService.addOrUpdate(userId, productId, quantity)) {
            return Result.success();
        }
        return Result.error(500, "添加失败");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        int quantity = Integer.parseInt(params.get("quantity").toString());
        if (cartService.updateQuantity(id, quantity)) {
            return Result.success();
        }
        return Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (cartService.delete(id)) {
            return Result.success();
        }
        return Result.error(500, "删除失败");
    }

    @DeleteMapping
    public Result<?> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clear(userId);
        return Result.success();
    }
}

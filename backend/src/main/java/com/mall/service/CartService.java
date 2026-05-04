package com.mall.service;

import com.mall.entity.CartItem;
import com.mall.mapper.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartMapper cartMapper;

    public CartService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    public List<CartItem> findByUserId(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    public boolean addOrUpdate(Long userId, Long productId, int quantity) {
        CartItem existing = cartMapper.findByUserAndProduct(userId, productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return cartMapper.updateQuantity(existing) > 0;
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        return cartMapper.insert(item) > 0;
    }

    public boolean updateQuantity(Long id, int quantity) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setQuantity(quantity);
        return cartMapper.updateQuantity(item) > 0;
    }

    public boolean delete(Long id) {
        return cartMapper.deleteById(id) > 0;
    }

    public boolean clear(Long userId) {
        return cartMapper.deleteByUserId(userId) >= 0;
    }
}

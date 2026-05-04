package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return Result.success(productService.search(keyword));
        }
        return Result.success(productService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(product);
    }
}

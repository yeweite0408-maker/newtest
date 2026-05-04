package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.service.ProductService;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/users")
    public Result<List<User>> users(HttpServletRequest request) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<List<User>>)(Result<?>) check;
        return Result.success(userService.findAllUsers());
    }

    @GetMapping("/products")
    public Result<List<Product>> products(HttpServletRequest request) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<List<Product>>)(Result<?>) check;
        return Result.success(productService.findAll());
    }

    @PostMapping("/products")
    public Result<Product> addProduct(HttpServletRequest request, @RequestBody Product product) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Product>)(Result<?>) check;
        if (productService.add(product)) {
            return Result.success(product);
        }
        return Result.error(500, "添加失败");
    }

    @PutMapping("/products/{id}")
    public Result<Product> updateProduct(HttpServletRequest request, @PathVariable Long id, @RequestBody Product product) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Product>)(Result<?>) check;
        product.setId(id);
        if (productService.update(product)) {
            return Result.success(product);
        }
        return Result.error(500, "更新失败");
    }

    @DeleteMapping("/products/{id}")
    public Result<?> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        Result<?> check = checkAdmin(request);
        if (check != null) return check;
        if (productService.delete(id)) {
            return Result.success();
        }
        return Result.error(500, "删除失败");
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Map<String, String>>)(Result<?>) check;
        if (file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            int idx = file.getOriginalFilename().lastIndexOf(".");
            String ext = idx > 0 ? file.getOriginalFilename().substring(idx) : ".jpg";
            String filename = UUID.randomUUID().toString() + ext;
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);

            return Result.success(Map.of("url", "/uploads/" + filename));
        } catch (IOException e) {
            return Result.error(500, "上传失败");
        }
    }

    @GetMapping("/check")
    public Result<?> check(HttpServletRequest request) {
        return checkAdmin(request) != null ? Result.forbidden() : Result.success();
    }

    private Result<?> checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.forbidden();
        }
        return null;
    }
}

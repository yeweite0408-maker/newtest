package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Article;
import com.mall.entity.User;
import com.mall.service.ArticleService;
import com.mall.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ArticleService articleService;

    public AdminController(UserService userService, ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping("/check")
    public Result<?> check(HttpServletRequest request) {
        return checkAdmin(request) != null ? Result.forbidden() : Result.success();
    }

    @GetMapping("/users")
    public Result<List<User>> users(HttpServletRequest request) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<List<User>>)(Result<?>) check;
        return Result.success(userService.findAllUsers());
    }

    @GetMapping("/articles")
    public Result<List<Article>> articles(HttpServletRequest request) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<List<Article>>)(Result<?>) check;
        return Result.success(articleService.findAll());
    }

    @PostMapping("/articles")
    public Result<Article> createArticle(HttpServletRequest request, @RequestBody Article article) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Article>)(Result<?>) check;
        Long userId = (Long) request.getAttribute("userId");
        article.setAuthorId(userId);
        if (article.getStatus() == null) article.setStatus("published");
        articleService.create(article);
        return Result.success(article);
    }

    @PutMapping("/articles/{id}")
    public Result<Article> updateArticle(HttpServletRequest request, @PathVariable Long id, @RequestBody Article article) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Article>)(Result<?>) check;
        article.setId(id);
        articleService.update(article);
        return Result.success(article);
    }

    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(HttpServletRequest request, @PathVariable Long id) {
        Result<?> check = checkAdmin(request);
        if (check != null) return (Result<Void>)(Result<?>) check;
        articleService.deleteById(id);
        return Result.success();
    }

    private Result<?> checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.forbidden();
        }
        return null;
    }
}

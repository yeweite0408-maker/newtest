package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Article;
import com.mall.service.ArticleService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    public ArticleController(ArticleService articleService, JwtUtil jwtUtil) {
        this.articleService = articleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<Article>> getPublished() {
        return Result.success(articleService.findPublished());
    }

    @GetMapping("/all")
    public Result<List<Article>> getAll(@RequestHeader("Authorization") String token) {
        jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(articleService.findAll());
    }

    @GetMapping("/category/{category}")
    public Result<List<Article>> getByCategory(@PathVariable String category) {
        return Result.success(articleService.findByCategory(category));
    }

    @GetMapping("/search")
    public Result<List<Article>> search(@RequestParam String keyword) {
        return Result.success(articleService.search(keyword));
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        Article article = articleService.findById(id);
        if (article == null) return Result.error(404, "文章不存在");
        return Result.success(article);
    }

    @PostMapping
    public Result<Article> create(@RequestBody Article article, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        article.setAuthorId(userId);
        if (article.getStatus() == null) article.setStatus("published");
        articleService.create(article);
        return Result.success(article);
    }

    @PutMapping("/{id}")
    public Result<Article> update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        articleService.update(article);
        return Result.success(article);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteById(id);
        return Result.success();
    }
}

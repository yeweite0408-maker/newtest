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

    @GetMapping("/tags")
    public Result<List<String>> tags() {
        return Result.success(articleService.findAllTags());
    }

    @GetMapping("/drafts")
    public Result<List<Article>> drafts(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(articleService.findDraftsByAuthor(userId));
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        Article article = articleService.findById(id);
        if (article == null) return Result.error(404, "文章不存在");
        return Result.success(article);
    }

    @PostMapping("/{id}/view")
    public Result<Void> addView(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<Boolean> toggleLike(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) return Result.error(401, "请先登录");
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        boolean liked = articleService.toggleLike(userId, id);
        return Result.success(liked);
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

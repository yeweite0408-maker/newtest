package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Article;
import com.mall.entity.User;
import com.mall.service.ArticleService;
import com.mall.service.UserService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, ArticleService articleService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.articleService = articleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<User>> list() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/me")
    public Result<User> me(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(userService.findById(userId));
    }

    @GetMapping("/profile/{userId}")
    public Result<Map<String, Object>> profile(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        List<Article> articles = articleService.findByAuthorId(userId);
        return Result.success(Map.of("user", user, "articles", articles));
    }

    @GetMapping("/{userId}/likes")
    public Result<List<Article>> likedArticles(@PathVariable Long userId) {
        return Result.success(articleService.findLikedByUserId(userId));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        if (oldPwd == null || newPwd == null || newPwd.length() < 6) {
            return Result.error(400, "密码至少6个字符");
        }
        boolean ok = userService.changePassword(userId, oldPwd, newPwd);
        if (!ok) return Result.error(400, "原密码错误");
        return Result.success();
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam String keyword) {
        return Result.success(userService.findAll().stream()
                .filter(u -> u.getUsername().contains(keyword))
                .toList());
    }
}

package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Comment;
import com.mall.service.CommentService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    public CommentController(CommentService commentService, JwtUtil jwtUtil) {
        this.commentService = commentService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/article/{articleId}")
    public Result<List<Comment>> getByArticle(@PathVariable Long articleId) {
        return Result.success(commentService.findByArticleId(articleId));
    }

    @PostMapping
    public Result<Comment> create(@RequestBody Comment comment, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        comment.setUserId(userId);
        commentService.create(comment);
        return Result.success(comment);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.deleteById(id);
        return Result.success();
    }
}

package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.User;
import com.mall.service.UserService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<User>> list() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/me")
    public Result<User> me(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        User user = userService.findById(userId);
        return Result.success(user);
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam String keyword) {
        return Result.success(userService.findAll().stream()
                .filter(u -> u.getUsername().contains(keyword))
                .toList());
    }
}

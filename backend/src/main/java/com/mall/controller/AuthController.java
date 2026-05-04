package com.mall.controller;

import com.mall.dto.LoginRequest;
import com.mall.dto.RegisterRequest;
import com.mall.dto.Result;
import com.mall.entity.User;
import com.mall.service.UserService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        User user = userService.login(req.getUsername(), req.getPassword());
        if (user == null) {
            return Result.error(400, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        userMap.put("role", user.getRole());
        return Result.success(Map.of("token", token, "user", userMap));
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().length() < 3) {
            return Result.error(400, "用户名至少3个字符");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            return Result.error(400, "密码至少6个字符");
        }
        User user = userService.register(req.getUsername(), req.getPassword(), req.getEmail());
        if (user == null) {
            return Result.error(400, "用户名已存在");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail() != null ? user.getEmail() : "");
        userMap.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
        userMap.put("role", user.getRole());
        return Result.success(Map.of("token", token, "user", userMap));
    }
}

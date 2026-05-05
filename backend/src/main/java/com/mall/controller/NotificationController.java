package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Notification;
import com.mall.mapper.NotificationMapper;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationMapper notificationMapper;
    private final JwtUtil jwtUtil;

    public NotificationController(NotificationMapper notificationMapper, JwtUtil jwtUtil) {
        this.notificationMapper = notificationMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<Notification>> list(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(notificationMapper.findByUserId(userId));
    }

    @GetMapping("/unread")
    public Result<Map<String, Integer>> unread(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(Map.of("count", notificationMapper.countUnread(userId)));
    }

    @PutMapping("/read")
    public Result<Void> markRead(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        notificationMapper.markAllRead(userId);
        return Result.success();
    }
}

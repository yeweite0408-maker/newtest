package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Message;
import com.mall.service.MessageService;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final JwtUtil jwtUtil;

    public MessageController(MessageService messageService, JwtUtil jwtUtil) {
        this.messageService = messageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<Message>> getMyMessages(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(messageService.findByUserId(userId));
    }

    @GetMapping("/conversation/{otherUserId}")
    public Result<List<Message>> getConversation(@PathVariable Long otherUserId, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(messageService.findConversation(userId, otherUserId));
    }

    @PostMapping
    public Result<Message> send(@RequestBody Message message, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        message.setFromUserId(userId);
        messageService.send(message);
        return Result.success(message);
    }

    @GetMapping("/unread")
    public Result<Map<String, Integer>> unreadCount(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(Map.of("count", messageService.countUnread(userId)));
    }

    @PutMapping("/read")
    public Result<Void> markRead(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        messageService.markAsRead(userId);
        return Result.success();
    }
}

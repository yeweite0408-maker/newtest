package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Follow;
import com.mall.mapper.FollowMapper;
import com.mall.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowMapper followMapper;
    private final JwtUtil jwtUtil;

    public FollowController(FollowMapper followMapper, JwtUtil jwtUtil) {
        this.followMapper = followMapper;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Result<List<Follow>> myFollows(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(followMapper.findFollowees(userId));
    }

    @PostMapping("/{followeeId}")
    public Result<Boolean> toggle(@PathVariable Long followeeId, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        if (followMapper.exists(userId, followeeId) > 0) {
            followMapper.delete(userId, followeeId);
            return Result.success(false);
        } else {
            followMapper.insert(userId, followeeId);
            return Result.success(true);
        }
    }

    @GetMapping("/count/{userId}")
    public Result<Map<String, Integer>> count(@PathVariable Long userId) {
        return Result.success(Map.of(
            "followers", followMapper.countFollowers(userId),
            "followees", followMapper.countFollowees(userId)
        ));
    }

    @GetMapping("/check/{followeeId}")
    public Result<Boolean> check(@PathVariable Long followeeId, @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(followMapper.exists(userId, followeeId) > 0);
    }
}

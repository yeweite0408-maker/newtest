package com.mall.controller;

import com.mall.dto.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final JdbcTemplate jdbc;

    public AnalyticsController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/view")
    public Result<Void> trackView(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Object articleId = body.get("articleId");
        String ip = request.getRemoteAddr();
        jdbc.update("INSERT INTO page_views(article_id, ip) VALUES (?, ?)", articleId, ip);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Long totalViews = jdbc.queryForObject("SELECT COUNT(*) FROM page_views", Long.class);
        Long totalVisitors = jdbc.queryForObject("SELECT COUNT(DISTINCT ip) FROM page_views", Long.class);
        Long todayViews = jdbc.queryForObject("SELECT COUNT(*) FROM page_views WHERE created_at >= CURRENT_DATE", Long.class);
        return Result.success(Map.of("totalViews", totalViews, "totalVisitors", totalVisitors, "todayViews", todayViews));
    }
}

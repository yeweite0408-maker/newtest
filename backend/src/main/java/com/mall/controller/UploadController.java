package com.mall.controller;

import com.mall.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }
        try {
            String baseDir = System.getProperty("user.dir");
            Path dir = Paths.get(baseDir, uploadPath).normalize();
            Files.createDirectories(dir);

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path dest = dir.resolve(filename);
            file.transferTo(dest.toFile());

            return Result.success(Map.of("url", "/uploads/" + filename));
        } catch (Exception e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
}

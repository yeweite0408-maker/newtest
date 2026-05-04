package com.mall.controller;

import com.mall.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + ext;
            File dest = new File(dir, filename);
            file.transferTo(dest);

            return Result.success(Map.of("url", "/uploads/" + filename));
        } catch (Exception e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
}

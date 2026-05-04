package com.mall.controller;

import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryMapper.findAll());
    }

    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}

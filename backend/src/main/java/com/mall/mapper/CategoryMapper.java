package com.mall.mapper;

import com.mall.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories ORDER BY id")
    List<Category> findAll();

    @Insert("INSERT INTO categories(name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(Long id);
}

package com.mall.mapper;

import com.mall.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserLikeMapper {
    @Select("SELECT COUNT(*) FROM user_likes WHERE user_id = #{userId} AND article_id = #{articleId}")
    int exists(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Insert("INSERT INTO user_likes(user_id, article_id) VALUES (#{userId}, #{articleId})")
    int insert(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Delete("DELETE FROM user_likes WHERE user_id = #{userId} AND article_id = #{articleId}")
    int delete(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN user_likes l ON a.id = l.article_id JOIN users u ON a.author_id = u.id WHERE l.user_id = #{userId} ORDER BY l.created_at DESC")
    List<Article> findLikedByUserId(Long userId);
}

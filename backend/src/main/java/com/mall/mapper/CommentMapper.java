package com.mall.mapper;

import com.mall.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT c.*, u.username, u.avatar FROM comments c JOIN users u ON c.user_id = u.id WHERE c.article_id = #{articleId} ORDER BY c.created_at ASC")
    List<Comment> findByArticleId(Long articleId);

    @Insert("INSERT INTO comments(article_id, user_id, content) VALUES (#{articleId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM comments WHERE article_id = #{articleId}")
    int deleteByArticleId(Long articleId);
}

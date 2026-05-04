package com.mall.mapper;

import com.mall.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id ORDER BY a.created_at DESC")
    List<Article> findAll();

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id WHERE a.id = #{id}")
    Article findById(Long id);

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id WHERE a.status = 'published' ORDER BY a.created_at DESC")
    List<Article> findPublished();

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id WHERE a.category = #{category} AND a.status = 'published' ORDER BY a.created_at DESC")
    List<Article> findByCategory(String category);

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id WHERE a.author_id = #{authorId} ORDER BY a.created_at DESC")
    List<Article> findByAuthorId(Long authorId);

    @Select("SELECT a.*, u.username as author_name FROM articles a JOIN users u ON a.author_id = u.id WHERE (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.content LIKE CONCAT('%', #{keyword}, '%')) AND a.status = 'published' ORDER BY a.created_at DESC")
    List<Article> search(String keyword);

    @Insert("INSERT INTO articles(title, content, summary, cover_image, category, tags, author_id, status) VALUES (#{title}, #{content}, #{summary}, #{coverImage}, #{category}, #{tags}, #{authorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);

    @Update("UPDATE articles SET title=#{title}, content=#{content}, summary=#{summary}, cover_image=#{coverImage}, category=#{category}, tags=#{tags}, status=#{status}, updated_at=CURRENT_TIMESTAMP WHERE id=#{id}")
    int update(Article article);

    @Delete("DELETE FROM articles WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE articles SET views_count = views_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Update("UPDATE articles SET likes_count = likes_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE articles SET likes_count = GREATEST(0, likes_count - 1) WHERE id = #{id}")
    int decrementLikeCount(Long id);
}

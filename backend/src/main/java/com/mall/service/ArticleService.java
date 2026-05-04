package com.mall.service;

import com.mall.entity.Article;
import com.mall.mapper.ArticleMapper;
import com.mall.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    public ArticleService(ArticleMapper articleMapper, CommentMapper commentMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
    }

    public List<Article> findAll() {
        return articleMapper.findAll();
    }

    public Article findById(Long id) {
        return articleMapper.findById(id);
    }

    public List<Article> findPublished() {
        return articleMapper.findPublished();
    }

    public List<Article> findByCategory(String category) {
        return articleMapper.findByCategory(category);
    }

    public List<Article> findByAuthorId(Long authorId) {
        return articleMapper.findByAuthorId(authorId);
    }

    public List<Article> search(String keyword) {
        return articleMapper.search(keyword);
    }

    public int create(Article article) {
        return articleMapper.insert(article);
    }

    public int update(Article article) {
        return articleMapper.update(article);
    }

    @Transactional
    public int deleteById(Long id) {
        commentMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
        return 1;
    }
}

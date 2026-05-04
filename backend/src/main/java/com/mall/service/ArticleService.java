package com.mall.service;

import com.mall.entity.Article;
import com.mall.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
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

    public int deleteById(Long id) {
        articleMapper.deleteById(id);
        return 1;
    }
}

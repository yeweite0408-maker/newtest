package com.mall.service;

import com.mall.entity.Article;
import com.mall.mapper.ArticleMapper;
import com.mall.mapper.CommentMapper;
import com.mall.mapper.UserLikeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final UserLikeMapper userLikeMapper;

    public ArticleService(ArticleMapper articleMapper, CommentMapper commentMapper, UserLikeMapper userLikeMapper) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.userLikeMapper = userLikeMapper;
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

    public void incrementViewCount(Long id) {
        articleMapper.incrementViewCount(id);
    }

    @Transactional
    public boolean toggleLike(Long userId, Long articleId) {
        if (userLikeMapper.exists(userId, articleId) > 0) {
            userLikeMapper.delete(userId, articleId);
            articleMapper.decrementLikeCount(articleId);
            return false;
        } else {
            userLikeMapper.insert(userId, articleId);
            articleMapper.incrementLikeCount(articleId);
            return true;
        }
    }

    public boolean isLiked(Long userId, Long articleId) {
        return userLikeMapper.exists(userId, articleId) > 0;
    }

    public List<Article> findDraftsByAuthor(Long authorId) {
        return articleMapper.findDraftsByAuthor(authorId);
    }

    public List<String> findAllTags() {
        return articleMapper.findAllTags();
    }

    public List<Article> findLikedByUserId(Long userId) {
        return userLikeMapper.findLikedByUserId(userId);
    }

    @Transactional
    public int deleteById(Long id) {
        commentMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
        return 1;
    }
}

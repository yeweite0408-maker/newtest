package com.mall.service;

import com.mall.entity.Comment;
import com.mall.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public List<Comment> findByArticleId(Long articleId) {
        return commentMapper.findByArticleId(articleId);
    }

    public int create(Comment comment) {
        return commentMapper.insert(comment);
    }

    public int deleteById(Long id) {
        return commentMapper.deleteById(id);
    }
}

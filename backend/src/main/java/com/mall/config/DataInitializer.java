package com.mall.config;

import com.mall.entity.Article;
import com.mall.entity.User;
import com.mall.mapper.ArticleMapper;
import com.mall.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, ArticleMapper articleMapper,
                           org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userMapper.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@blog.com");
            admin.setRole("admin");
            userMapper.insert(admin);
            System.out.println("Admin created: admin / admin");
        }

        if (userMapper.findByUsername("test") == null) {
            User test = new User();
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("123456"));
            test.setEmail("test@blog.com");
            test.setRole("user");
            userMapper.insert(test);
            System.out.println("User created: test / 123456");
        }

        if (articleMapper.findAll().isEmpty()) {
            User admin = userMapper.findByUsername("admin");
            seedArticles(admin.getId());
            System.out.println("Sample articles seeded.");
        }
    }

    private void seedArticles(Long authorId) {
        insertArticle(authorId, "Spring Boot 入门指南", "Spring Boot 是当前 Java 后端开发最流行的框架之一。它基于 Spring 框架，通过自动配置和约定大于配置的理念，让开发者能够快速搭建生产级别的应用。\n\n本文将从零开始，带你了解 Spring Boot 的核心概念、项目结构、以及如何创建一个 RESTful API。", "从零开始学习 Spring Boot，构建你的第一个 Java Web 应用", "技术分享", "Spring Boot,Java");
        insertArticle(authorId, "Vue 3 + Element Plus 实战", "Vue 3 引入了 Composition API，让组件逻辑复用更加灵活。搭配 Element Plus 组件库，可以快速构建出美观的后台管理系统。\n\n本文将介绍 Vue 3 的核心特性，包括 ref、reactive、computed、watch 等 Composition API 的使用方法，以及如何与 Element Plus 集成开发。", "掌握 Vue 3 组合式 API，配合 Element Plus 构建优雅的 UI", "前端开发", "Vue,Element Plus");
        insertArticle(authorId, "个人博客搭建全记录", "从需求分析到技术选型，从数据库设计到前后端开发，本文将完整记录一个个人博客系统的搭建过程。\n\n技术栈：Spring Boot + Vue 3 + Element Plus + H2 Database。功能包括：文章管理、评论系统、私信功能、文件上传等。", "记录一次完整的个人博客开发历程，分享技术选型与实现思路", "项目实战", "博客,全栈,Spring Boot,Vue");
        insertArticle(authorId, "MySQL vs H2：如何选择数据库", "在项目开发初期，选择合适的数据库是一个重要决策。MySQL 是生产环境的主流选择，而 H2 作为内存数据库，在开发和测试阶段有其独特优势。\n\n本文对比两种数据库的特点、适用场景，以及如何在 Spring Boot 中配置和切换。", "对比分析 MySQL 和 H2 数据库的优劣与适用场景", "技术分享", "数据库,MySQL,H2");
        insertArticle(authorId, "Git 协作开发最佳实践", "团队协作开发中，Git 是必不可少的工具。但很多开发者在实际使用中会遇到各种问题：冲突解决、分支管理、代码审查等。\n\n本文将分享 Git 工作流的最佳实践，包括分支策略、commit 规范、以及如何高效地进行 Code Review。", "掌握 Git 团队协作的核心技巧，提升开发效率", "开发工具", "Git,团队协作,工作流");
    }

    private void insertArticle(Long authorId, String title, String content, String summary, String category, String tags) {
        Article a = new Article();
        a.setTitle(title);
        a.setContent(content);
        a.setSummary(summary);
        a.setCategory(category);
        a.setTags(tags);
        a.setAuthorId(authorId);
        a.setStatus("published");
        articleMapper.insert(a);
    }
}

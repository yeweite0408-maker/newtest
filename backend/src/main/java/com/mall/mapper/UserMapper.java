package com.mall.mapper;

import com.mall.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO users(username, password, email, role) VALUES (#{username}, #{password}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM users WHERE role = 'user'")
    java.util.List<User> findAllUsers();
}

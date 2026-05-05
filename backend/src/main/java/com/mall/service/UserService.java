package com.mall.service;

import com.mall.entity.User;
import com.mall.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User register(String username, String password, String email) {
        if (userMapper.findByUsername(username) != null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole("user");
        userMapper.insert(user);
        return user;
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public List<User> findAllUsers() {
        return userMapper.findAllUsers();
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public void updateBio(Long userId, String bio) {
        userMapper.updateBio(userId, bio);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) return false;
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) return false;
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updatePassword(user);
        return true;
    }
}

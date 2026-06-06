package com.jypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jypt.entity.User;
import com.jypt.mapper.UserMapper;
import com.jypt.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return
     */
    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 用户注册
     * @param user 待注册的用户信息
     * @return 注册成功返回 true，失败返回 false
     */
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        // 密码加密：使用 MD5 对密码进行加密处理
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            user.getPassword().getBytes(StandardCharsets.UTF_8)
        );
        user.setPassword(encryptedPassword);
        // 保存用户信息到数据库
        return save(user);
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回 null
     */
    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = getUserByUsername(username);
        if (user == null) {
            return null;
        }
        // 对输入的密码进行 MD5 加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            password.getBytes(StandardCharsets.UTF_8)
        );
        // 比较加密后的密码与数据库中存储的密码是否一致
        if (user.getPassword().equals(encryptedPassword)) {
            return user;
        }
        return null;
    }
}

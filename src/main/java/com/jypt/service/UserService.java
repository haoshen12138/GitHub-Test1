package com.jypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jypt.entity.User;

public interface UserService extends IService<User> {
    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户实体对象，如果未找到则返回null
     */
    User getUserByUsername(String username);

    /**
     * 用户注册
     *
     * @param user 待注册的用户信息
     * @return 注册是否成功
     */
    boolean register(User user);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户实体对象，登录失败则返回null
     */
    User login(String username, String password);
}

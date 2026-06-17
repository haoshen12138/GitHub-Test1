package com.jypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jypt.entity.User;

public interface UserService extends IService<User> {
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户实体对象，如果未找到则返回null
     */
    User getUserByUsername(String username);

    /**
     * 用户注册
     * @param user 待注册的用户信息
     * @return 注册是否成功
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户实体对象，登录失败则返回null
     */
    User login(String username, String password);

    /**
     * 修改密码（验证旧密码后更新为新密码）
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文，内部MD5加密后比对）
     * @param newPassword 新密码（明文，内部MD5加密后存储）
     * @return 修改成功返回 true
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新个人资料（仅允许修改昵称、手机、邮箱、头像）
     * @param user 包含id及待修改字段的用户对象
     * @return 更新后的用户对象
     */
    User updateProfile(User user);
}

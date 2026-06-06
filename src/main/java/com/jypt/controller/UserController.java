package com.jypt.controller;

import com.jypt.common.Result;
import com.jypt.entity.User;
import com.jypt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
/**
 * 用户控制器
 */
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册（自动设置默认密码123456）
     * @param user 待注册的用户信息（不包含密码）
     * @return 注册结果，成功返回"注册成功"，失败返回"用户名已存在"
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 设置默认密码为123456
        user.setPassword("123456");

        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    /**
     * 根据用户名和密码登录
     * @param user 包含用户名和密码的用户信息
     * @return 登录结果，成功返回用户信息，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // 返回用户信息（实际项目中应该返回token）
            return Result.success(loggedInUser);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 根据id查询用户信息
     * @param id 用户ID
     * @return 查询结果，成功返回用户信息，失败返回错误信息
     */
    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
}

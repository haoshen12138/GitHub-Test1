package com.jypt.controller;

import com.jypt.common.Result;
import com.jypt.entity.User;
import com.jypt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * @param user 待注册的用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword("123456");
        boolean success = userService.register(user);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    /**
     * 根据用户名和密码登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            return Result.success(loggedInUser);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 根据id查询用户信息
     */
    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/select")
    public Result<?> getUserByUsername(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    /**
     * 修改个人资料（仅允许昵称、手机、邮箱、头像）
     * 禁止通过此接口修改密码和用户名
     */
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        try {
            User updated = userService.updateProfile(user);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     * 请求体：{ "userId": 1, "oldPassword": "123456", "newPassword": "654321" }
     */
    @PutMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> request) {
        String userIdStr = request.get("userId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (userIdStr == null || oldPassword == null || newPassword == null) {
            return Result.error("参数不完整：需要 userId、oldPassword、newPassword");
        }
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        try {
            boolean success = userService.changePassword(
                    Long.valueOf(userIdStr), oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            }
            return Result.error("密码修改失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}

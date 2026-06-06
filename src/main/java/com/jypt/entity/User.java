package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
/**
 * 用户表
 */
public class User {
    @TableId(type = IdType.AUTO)
    /**
     * 用户ID，主键，自增
     */
    private Long id;

    /**
     * 用户名，用于登录
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户昵称，用于显示
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户头像URL
     */
    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}

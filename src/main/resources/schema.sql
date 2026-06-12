-- src/main/resources/schema.sql

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title` VARCHAR(100) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `category` VARCHAR(50) COMMENT '分类',
    `status` VARCHAR(20) DEFAULT '出售中' COMMENT '状态:出售中,已售出,已下架',
    `images` TEXT COMMENT '图片URL(多个用逗号分隔)',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_product_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 在现有表结构后添加以下内容


-- 创建商品分类表
CREATE TABLE IF NOT EXISTS `category` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                          `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_name` (`name`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';


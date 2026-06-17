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
                                          `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_name` (`name`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ========== 创建交易表 ==========
CREATE TABLE IF NOT EXISTS `transaction` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '交易ID',
                                             `transaction_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '交易编号',
                                             `product_id` BIGINT NOT NULL COMMENT '商品ID',
                                             `product_title` VARCHAR(100) COMMENT '商品标题（冗余字段，方便查询展示）',
                                             `price` DECIMAL(10,2) NOT NULL COMMENT '交易价格',
                                             `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
                                             `seller_name` VARCHAR(50) COMMENT '卖家用户名',
                                             `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
                                             `buyer_name` VARCHAR(50) COMMENT '买家用户名',
                                             `status` VARCHAR(20) NOT NULL DEFAULT '待付款' COMMENT '交易状态：待付款、待发货、待收货、已完成、已取消',
                                             `payment_status` VARCHAR(20) NOT NULL DEFAULT '未支付' COMMENT '支付状态：未支付、已支付',
                                             `trading_location` VARCHAR(200) COMMENT '交易地点',
                                             `payment_time` DATETIME COMMENT '付款时间',
                                             `shipping_time` DATETIME COMMENT '发货时间',
                                             `receive_time` DATETIME COMMENT '收货时间',
                                             `complete_time` DATETIME COMMENT '完成时间',
                                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `buyer_remark` VARCHAR(500) COMMENT '买家备注',
                                             `seller_remark` VARCHAR(500) COMMENT '卖家备注',
                                             `cancel_reason` VARCHAR(500) COMMENT '取消原因',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_transaction_no` (`transaction_no`),
                                             KEY `idx_buyer_id` (`buyer_id`),
                                             KEY `idx_seller_id` (`seller_id`),
                                             KEY `idx_product_id` (`product_id`),
                                             KEY `idx_status` (`status`),
                                             KEY `idx_payment_status` (`payment_status`),
                                             CONSTRAINT `fk_transaction_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
                                             CONSTRAINT `fk_transaction_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`),
                                             CONSTRAINT `fk_transaction_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易表';

-- 购物车表结构
CREATE TABLE IF NOT EXISTS `cart` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
                                      `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                      `product_id` BIGINT NOT NULL COMMENT '商品ID',
                                      `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
                                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0:未删除,1:已删除)',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_user_id` (`user_id`),
                                      KEY `idx_product_id` (`product_id`),
                                      KEY `idx_seller_id` (`seller_id`),
                                      UNIQUE KEY `uk_user_product` (`user_id`, `product_id`, `deleted`) COMMENT '用户-商品唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 购物车表结构
CREATE TABLE IF NOT EXISTS `cart` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
                                      `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                      `product_id` BIGINT NOT NULL COMMENT '商品ID',
                                      `product_title` VARCHAR(100) COMMENT '商品标题（冗余字段）',
                                      `price` DECIMAL(10,2) COMMENT '商品价格（冗余字段）',
                                      `product_image` VARCHAR(500) COMMENT '商品图片（冗余字段）',
                                      `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
                                      `seller_name` VARCHAR(50) COMMENT '卖家用户名',
                                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志(0:未删除,1:已删除)',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_user_id` (`user_id`),
                                      KEY `idx_product_id` (`product_id`),
                                      KEY `idx_seller_id` (`seller_id`),
                                      UNIQUE KEY `uk_user_product` (`user_id`, `product_id`, `deleted`) COMMENT '用户-商品唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

# -- 购物车表结构
# CREATE TABLE IF NOT EXISTS `cart` (
#     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID，主键，自增',
#     `user_id` BIGINT NOT NULL COMMENT '用户ID',
#     `product_id` BIGINT NOT NULL COMMENT '商品ID',
#     `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
#     `seller_name` VARCHAR(100) NOT NULL COMMENT '卖家用户名',
#     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识(0:未删除,1:已删除)',
#     PRIMARY KEY (`id`),
#     UNIQUE KEY `uk_user_product` (`user_id`, `product_id`, `deleted`) COMMENT '用户-商品唯一索引，防止重复添加',
#     KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
#     KEY `idx_product_id` (`product_id`) COMMENT '商品ID索引',
#     KEY `idx_seller_id` (`seller_id`) COMMENT '卖家ID索引'
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
#
# -- 为 cart 表添加缺失的字段
# ALTER TABLE `cart`
#     ADD COLUMN `product_title` VARCHAR(100) COMMENT '商品标题（冗余字段，方便查询展示）' AFTER `product_id`,
#     ADD COLUMN `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格（记录加入购物车时的价格）' AFTER `product_title`,
#     ADD COLUMN `product_image` VARCHAR(500) COMMENT '商品图片URL（冗余字段）' AFTER `price`,
#     ADD COLUMN `seller_name` VARCHAR(50) COMMENT '卖家用户名' AFTER `seller_id`;

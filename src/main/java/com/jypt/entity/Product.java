package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
/**
 * 商品表
 */
public class Product {
    @TableId(type = IdType.AUTO)
    /**
     * 商品唯一标识，主键，自增
     */
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格，使用 BigDecimal 保证精度
     */
    private BigDecimal price;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品状态：出售中、已售出、已下架
     */
    private String status;

    /**
     * 商品图片，可存储多个图片地址，通常用逗号分隔或 JSON 字符串
     */
    private String images;

    /**
     * 发布者用户 ID，关联用户表
     */
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}

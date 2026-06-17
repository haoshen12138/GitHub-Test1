package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("cart")
public class Cart {
    /**
     * 购物车ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;
    /**
     * 商品标题（冗余字段，方便查询展示）
     */
    @TableField("product_title")
    private String productTitle;
    /**
     * 商品价格（冗余字段，记录加入购物车时的价格）
     */
    @TableField("price")
    private BigDecimal price;
    /**
     * 商品图片URL（冗余字段）
     */
    @TableField("product_image")
    private String productImage;
    /**
     * 卖家ID
     */
    @TableField("seller_id")
    private Long sellerId;
    /**
     * 卖家用户名
     */
    @TableField("seller_name")
    private String sellerName;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识字段。
     * 使用 @TableLogic 注解标记，表示启用 MyBatis-Plus 的逻辑删除功能。
     * 当 deleted 值为 0 时表示记录未被删除，为 1 时表示记录已被逻辑删除。
     * 数据库查询时会自动过滤掉已逻辑删除的记录（deleted = 1）。
     */
    @TableLogic
    private Integer deleted;
}

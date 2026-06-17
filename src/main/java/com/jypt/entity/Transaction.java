package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易实体类
 */
@Data
@TableName("transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String transactionNo;      // 交易编号
    private Long productId;            // 商品ID
    private String productTitle;       // 商品标题（冗余字段）
    private BigDecimal price;          // 交易价格

    private Long sellerId;             // 卖家ID
    private String sellerName;         // 卖家用户名
    private Long buyerId;              // 买家ID
    private String buyerName;          // 买家用户名

    private String status;             // 状态：待付款、待发货、待收货、已完成、已取消
    private String paymentStatus;      // 支付状态：未支付、已支付

    private String tradingLocation ;    // 交易地点

    private LocalDateTime paymentTime;     // 付款时间
    private LocalDateTime shippingTime;    // 发货时间
    private LocalDateTime receiveTime;     // 收货时间
    private LocalDateTime completeTime;    // 完成时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String buyerRemark;        // 买家备注
    private String sellerRemark;       // 卖家备注
    private String cancelReason;       // 取消原因

}

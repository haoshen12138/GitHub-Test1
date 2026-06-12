package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    private BigDecimal price;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String status;

    private String images;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}

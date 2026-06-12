package com.jypt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@TableName("category")
/**
 * 分类实体类
 */
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}

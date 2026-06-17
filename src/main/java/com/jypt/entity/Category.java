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
        /**
     * 主键ID，使用数据库自增策略生成
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称，不能为空
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 创建时间，在数据插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，在数据插入和更新时自动填充
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

package com.jypt.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime; // 添加 import 语句

@Component
/**
 * 自定义元数据处理器，用于自动填充创建时间和更新时间字段
 */
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入数据时自动填充字段
     * @param metaObject 元数据对象，用于操作实体类的属性
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 使用严格模式填充创建时间，确保字段类型匹配
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 使用严格模式填充更新时间，插入时与创建时间相同
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新数据时自动填充字段
     * @param metaObject 元数据对象，用于操作实体类的属性
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 使用严格模式填充更新时间，更新时设置为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}

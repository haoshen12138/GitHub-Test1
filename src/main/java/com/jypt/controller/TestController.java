package com.jypt.controller;

import com.jypt.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     * @return
     */
    @GetMapping("/db")
    public Result<?> testDatabase() {
        try {
            // 测试数据库连接
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT COUNT(*) as count "); // 添加反引号
            List<Map<String, Object>> products = jdbcTemplate.queryForList("SELECT COUNT(*) as count"); // 添加反引号

            return Result.success(String.format(
                "数据库连接成功！用户表记录数：%s，商品表记录数：%s",
                users.get(0).get("count"),
                products.get(0).get("count")
            ));
        } catch (Exception e) {
            return Result.error("数据库连接失败：" + e.getMessage());
        }
    }
}

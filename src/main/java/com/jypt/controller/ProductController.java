package com.jypt.controller;

import com.jypt.common.Result;
import com.jypt.entity.Product;
import com.jypt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
/**
 * 商品控制器
 */
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 发布商品
     */
    @PostMapping("/publish")
    public Result<?> publishProduct(@RequestBody Product product) {
        boolean success = productService.publishProduct(product);
        if (success) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    /**
     * 获取所有商品信息
     */
    @GetMapping("/all")
    public Result<?> getAllProducts() {
        List<Product> products = productService.list();
        return Result.success(products);
    }

    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    public Result<?> getProductDetail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("商品不存在");
    }

    /**
     * 根据商品名关键字搜索商品
     */
    @GetMapping("/search")
    public Result<?> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Result.success(products);
    }


    // ProductController.java 中添加新方法
    /**
     * 根据分类ID获取商品（新方法）
     */
    @GetMapping("/category/id/{categoryId}")
    public Result<?> getProductsByCategoryId(@PathVariable Long categoryId) {
        // 需要先在 ProductService 中添加 getProductsByCategoryId 方法
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return Result.success(products);
    }

    /**
     * 根据价格范围获取商品
     */
    @GetMapping("/price-range")
    public Result<?> getProductsByPriceRange(@RequestParam BigDecimal minPrice,
                                             @RequestParam BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return Result.success(products);
    }


    /**
     * 根据用户ID获取用户发布的商品列表
     * @param userId 用户ID，不能为空
     * @return 包含用户发布商品列表的结果对象
     * @apiNote 通过用户ID查询该用户发布的所有商品
     * @example GET /api/product/user/1
     */
    @GetMapping("/user/{userId}")
    public Result<?> getUserProducts(@PathVariable Long userId) {
        // 参数验证
        if (userId == null || userId <= 0) {
            return Result.error("用户ID不能为空且必须大于0");
        }
        try {
            // 调用服务层方法获取用户发布的商品列表
            List<Product> products = productService.getUserProductsByUserId(userId);

            // 检查结果
            if (products == null || products.isEmpty()) {
                return Result.success("该用户暂无发布的商品"); // 只返回消息
            }
            return Result.success(products);
        } catch (Exception e) {
            // 记录异常日志（实际项目中应使用日志框架）
            // log.error("获取用户商品列表失败，用户ID: {}", userId, e);
            return Result.error("获取用户商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping("/update")
    public Result<?> updateProduct(@RequestBody Product product) {
        boolean success = productService.updateById(product);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }


    /**
     * 更新商品状态,如果是出售中则改为已售出，如果已售出则改为出售中
     */
    @PutMapping("/status/{id}")
    public Result<?> updateProductStatus(@PathVariable Long id) {
        // 获取商品信息
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 更新商品状态
        String status = product.getStatus();
        if ("出售中".equals(status)) {
            product.setStatus("已售出");
        } else if ("已售出".equals(status)) {
            product.setStatus("出售中");
        } else {
            return Result.error("商品状态不合法");
        }
        // 更新商品信息
        boolean success = productService.updateById(product);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除商品（逻辑删除）删除前后加检查信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Long id) {
        // 检查商品是否存在
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 删除商品
        boolean success = productService.deleteProductById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }


}

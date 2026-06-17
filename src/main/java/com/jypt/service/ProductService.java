package com.jypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jypt.common.Result;
import com.jypt.entity.Product;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 发布商品
     */
    boolean publishProduct(Product product);

    /**
     * 根据关键字搜索商品
     */
    List<Product> searchProducts(String keyword);

    /**
     * 根据分类获取商品
     */
    List<Product> getProductsByCategory(String category);

    /**
     * 根据价格范围获取商品
     */
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 获取用户发布的商品列表
     */
    List<Product> getUserProducts(Long userId);

    /**
     * 更新商品状态
     */
    boolean updateProductStatus(Long productId, String status);

    /**
     * 获取热门商品（按浏览量排序）
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 获取最新商品
     */
    List<Product> getLatestProducts(Integer limit);

    /**
     * 根据分类ID获取商品（新方法）
     * @param categoryId
     * @return
     */
    List<Product> getProductsByCategoryId(Long categoryId);

    /**
     * 获取用户发布的商品列表
     */
    List<Product> getUserProductsByUserId(Long userId);

    /**
     * 通过商品Id删除商品
     */
    boolean deleteProductById(Long id);



}

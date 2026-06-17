package com.jypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jypt.common.Result;
import com.jypt.entity.Product;
import com.jypt.mapper.ProductMapper;
import com.jypt.service.ProductService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {

    @Override
    public boolean publishProduct(Product product) {
        // 设置默认状态为出售中
        product.setStatus("出售中");
        return this.save(product);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Product::getTitle, keyword)
                   .or()
                   .like(Product::getDescription, keyword);
        }

        // 只显示未删除的商品
        wrapper.eq(Product::getDeleted, 0);

        // 按创建时间倒序排列
        wrapper.orderByDesc(Product::getCreateTime);

        return this.list(wrapper);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategory, category)
               .eq(Product::getDeleted, 0)
               .orderByDesc(Product::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }

        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }

        wrapper.eq(Product::getDeleted, 0)
               .orderByDesc(Product::getCreateTime);

        return this.list(wrapper);
    }

    @Override
    public List<Product> getUserProducts(Long userId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getUserId, userId)
               .eq(Product::getDeleted, 0)
               .orderByDesc(Product::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean updateProductStatus(Long productId, String status) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setStatus(status);
            return this.updateById(product);
        }
        return false;
    }


    @Override
    public List<Product> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0)
               .orderByDesc(Product::getCreateTime)
               .last("LIMIT " + limit);
        return this.list(wrapper);
    }


    @Override
    public List<Product> getLatestProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0)
               .orderByDesc(Product::getCreateTime)
               .last("LIMIT " + limit);
        return this.list(wrapper);
    }

    /**
     * 根据分类ID获取商品（新方法）
     * @param categoryId
     * @return
     */
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, categoryId)  // 使用 category_id 字段
                .eq(Product::getDeleted, 0)
                .orderByDesc(Product::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 获取用户发布的商品列表
     * @param userId
     * @return
     */
    @Override
    public List<Product> getUserProductsByUserId(Long userId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getUserId, userId)
                .eq(Product::getDeleted, 0)
                .orderByDesc(Product::getCreateTime);
                return this.list(wrapper);
    }
    /**
     * 通过商品Id删除商品
     * @param id
     * @return
     */
    @Override
    public boolean deleteProductById(Long id) {
        // 检查商品是否存在
        Product product = this.getById(id);
        //如果商品不存在，则返回false
        if (product == null) {
            return false;
        }
        //如果商品存在，则删除商品
        return this.removeById(id);
    }

}

package com.jypt.service;

import com.jypt.entity.Category;
import java.util.List;

public interface CategoryService {
        /**
     * 创建新的商品分类
     * @param category 待创建的商品分类实体对象
     * @return 创建成功后的商品分类实体对象（包含生成的ID等信息）
     */
    Category createCategory(Category category);

    /**
     * 根据ID查询商品分类
     * @param id 商品分类的唯一标识符
     * @return 对应的商品分类实体对象，若不存在则返回null
     */
    Category getCategoryById(Long id);

    /**
     * 获取所有商品分类列表
     * @return 包含所有商品分类的列表
     */
    List<Category> getAllCategories();

    /**
     * 更新商品分类信息
     * @param category 包含更新信息的商品分类实体对象
     * @return 更新后的商品分类实体对象
     */
    Category updateCategory(Category category);

    /**
     * 根据ID删除商品分类
     * @param id 待删除的商品分类的唯一标识符
     */
    void deleteCategory(Long id);

    /**
     * 根据分类名称搜索商品分类
     * @param name 分类名称（支持模糊匹配）
     * @return 匹配指定名称的商品分类列表
     */
    List<Category> searchCategoriesByName(String name);
}

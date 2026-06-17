package com.jypt.service.impl;

import com.jypt.entity.Category;
import com.jypt.mapper.CategoryMapper;
import com.jypt.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public Category createCategory(Category category) {
        this.save(category);
        return category;
    }

    @Override
    public Category getCategoryById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return this.list();
    }

    @Override
    public Category updateCategory(Category category) {
        this.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        this.removeById(id);
    }

    @Override
    public List<Category> searchCategoriesByName(String name) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Category::getName, name);
        return this.list(queryWrapper);
    }
}

package com.jypt.controller;

import com.jypt.entity.Category;
import com.jypt.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 * 提供分类相关的 RESTful API 接口
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 创建新的分类
     * @param category 分类实体对象，包含分类名称等信息
     * @return 创建成功的分类信息
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类详情信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 更新分类信息
     * @param id 分类ID
     * @param category 更新后的分类信息
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @Valid @RequestBody Category category) {
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * 删除分类（逻辑删除）
     * @param id 分类ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

//    /**
//     * 根据名称搜索分类
//     * @param name 分类名称（支持模糊查询）
//     * @return 匹配的分类列表
//     */
//    @GetMapping("/search")
//    public ResponseEntity<List<Category>> searchCategoriesByName(@RequestParam String name) {
//        List<Category> categories = categoryService.searchCategoriesByName(name);
//        return ResponseEntity.ok(categories);
//    }
}

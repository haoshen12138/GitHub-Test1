package com.jypt.controller;

import com.jypt.common.Result;
import com.jypt.entity.Cart;
import com.jypt.service.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;



    /**
     * 添加商品到购物车
     * @param cart 购物车信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> addToCart(@RequestBody Cart cart) {
        // 检查是否已存在
        boolean exists = cartService.checkProductExists(cart.getUserId(), cart.getProductId());
        if (exists) {
            return Result.error("该商品已在购物车中");
        }
//         检查是否是自己发布的商品
//         这里需要根据您的业务逻辑实现，比如通过商品ID查询卖家ID
//         boolean isOwnProduct = productService.isOwnProduct(cart.getUserId(), cart.getProductId());
//         if (isOwnProduct) {
//             return Result.error("不能购买自己发布的商品");
//         }

        boolean success = cartService.save(cart);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    @GetMapping("/list/{userId}")

    public Result<List<Cart>> getCartList(@PathVariable Long userId) {
        List<Cart> cartList = cartService.getCartByUserId(userId);
        return Result.success(cartList);
    }

    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param quantity 商品数量
     * @return 操作结果
     */
    @PutMapping("/update/{cartId}")

    public Result<Boolean> updateQuantity(
            @PathVariable Long cartId,
            @RequestParam Integer quantity) {
        boolean success = cartService.updateQuantity(cartId, quantity);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 批量删除购物车商品
     * @param cartIds 购物车ID列表
     * @return 操作结果
     */
    @DeleteMapping("/delete/batch")

    public Result<Boolean> batchDelete(@RequestBody List<Long> cartIds) {
        boolean success = cartService.batchDelete(cartIds);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/clear/{userId}")

    public Result<Boolean> clearCart(@PathVariable Long userId) {
        boolean success = cartService.clearCart(userId);
        return success ? Result.success("清空成功") : Result.error("清空失败");
    }

    /**
     * 根据ID获取购物车信息
     * @param id 购物车ID
     * @return 购物车信息
     */
    @GetMapping("/{id}")

    public Result<Cart> getById(@PathVariable Long id) {
        Cart cart = cartService.getById(id);
        return cart != null ? Result.success(cart) : Result.error("购物车信息不存在");
    }

    /**
     * 更新购物车信息
     * @param cart 购物车信息
     * @return 操作结果
     */
    @PutMapping("/update")

    public Result<Boolean> update(@RequestBody Cart cart) {
        boolean success = cartService.updateById(cart);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除购物车商品
     * @param id 购物车ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")

    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = cartService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 检查商品是否在购物车中
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否在购物车中
     */
    @GetMapping("/check")
    public Result<Boolean> checkProductInCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        boolean exists = cartService.checkProductExists(userId, productId);
        return Result.success(exists);
    }

    /**
     * 获取购物车商品数量
     * @param userId 用户ID
     * @return 商品数量
     */
    @GetMapping("/count/{userId}")
    public Result<Integer> getCartCount(@PathVariable Long userId) {
        int count = cartService.getCartCountByUserId(userId);
        return Result.success(count);
    }


}

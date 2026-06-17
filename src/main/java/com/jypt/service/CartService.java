package com.jypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jypt.entity.Cart;
import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     * @param cart 购物车信息
     * @return 是否添加成功
     */
    boolean addToCart(Cart cart);

    /**
     * 根据用户ID获取购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> getCartByUserId(Long userId);

    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param quantity 商品数量
     * @return 是否更新成功
     */
    boolean updateQuantity(Long cartId, Integer quantity);

    /**
     * 批量删除购物车商品
     * @param cartIds 购物车ID列表
     * @return 是否删除成功
     */
    boolean batchDelete(List<Long> cartIds);

    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearCart(Long userId);
    /**
     * 检查商品是否在购物车中
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否存在
     */
    boolean checkProductExists(Long userId, Long productId);

    /**
     * 获取用户购物车中的商品数量
     * @param userId 用户ID
     * @return 商品数量
     */
    int getCartCountByUserId(Long userId);
}

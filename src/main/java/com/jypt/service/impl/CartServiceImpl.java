package com.jypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jypt.entity.Cart;
import com.jypt.mapper.CartMapper;
import com.jypt.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;

    @Override
    public boolean addToCart(Cart cart) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, cart.getUserId())
                   .eq(Cart::getProductId, cart.getProductId())
                   .eq(Cart::getDeleted, 0);

        Cart existingCart = this.getOne(queryWrapper);
        if (existingCart != null) {
            existingCart.setUpdateTime(null);
            return this.updateById(existingCart);
        }
        return this.save(cart);
    }

    @Override
    public List<Cart> getCartByUserId(Long userId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                   .eq(Cart::getDeleted, 0)
                   .orderByDesc(Cart::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateQuantity(Long cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart == null) {
            return false;
        }
        cart.setUpdateTime(null);
        return this.updateById(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> cartIds) {
        return this.removeByIds(cartIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearCart(Long userId) {
        // 清空购物车：直接物理删除该用户所有记录，避免唯一键冲突
        int deleted = cartMapper.forceDeleteAllByUserId(userId);
        return deleted > 0;
    }

    @Override
    public boolean checkProductExists(Long userId, Long productId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId)
                .eq(Cart::getDeleted, 0);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public int getCartCountByUserId(Long userId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getDeleted, 0);
        return (int) this.count(queryWrapper);
    }

    /**
     * 重写 removeById：软删除前先物理删除已存在的同用户+同商品的已删除记录，
     * 避免 uk_user_product (user_id, product_id, deleted) 唯一键冲突
     */
    @Override
    public boolean removeById(Serializable id) {
        Cart cart = this.getById(id);
        if (cart != null) {
            // 物理删除已存在的 deleted=1 的重复记录
            cartMapper.physicalDeleteDuplicate(cart.getUserId(), cart.getProductId());
        }
        return super.removeById(id);
    }
}

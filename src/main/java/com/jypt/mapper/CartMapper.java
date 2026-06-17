package com.jypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jypt.entity.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 物理删除指定用户+商品的已软删除重复记录
     * 解决：删了又加、加了再删时 uk_user_product 唯一键冲突
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 1")
    int physicalDeleteDuplicate(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 物理删除用户的所有购物车记录（清空购物车用）
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int forceDeleteAllByUserId(@Param("userId") Long userId);
}

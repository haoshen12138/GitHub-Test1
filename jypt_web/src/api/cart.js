// jypt_web/src/api/cart.js
import request from '@/utils/request'

// 添加商品到购物车 — POST /api/cart/add
export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

// 获取用户购物车列表 — GET /api/cart/list/{userId}
export function getCartList(userId) {
  return request({
    url: `/cart/list/${userId}`,
    method: 'get'
  })
}

// 删除购物车商品 — DELETE /api/cart/delete/{id}
export function deleteCartItem(id) {
  return request({
    url: `/cart/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除购物车商品 — DELETE /api/cart/delete/batch
export function batchDeleteCartItems(cartIds) {
  return request({
    url: '/cart/delete/batch',
    method: 'delete',
    data: cartIds
  })
}

// 清空用户购物车 — DELETE /api/cart/clear/{userId}
export function clearCart(userId) {
  return request({
    url: `/cart/clear/${userId}`,
    method: 'delete'
  })
}

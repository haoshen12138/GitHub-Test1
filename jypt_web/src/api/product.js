           // jypt_web/src/api/product.js
import request from '@/utils/request'

// 获取所有商品 — GET /api/product/all
export function getAllProducts() {
  return request({
    url: '/product/all',
    method: 'get'
  })
}

// 根据 ID 获取商品详情 — GET /api/product/{id}
export function getProductById(id, silentError = false) {
  return request({
    url: `/product/${id}`,
    method: 'get',
    silentError
  })
}

// 搜索商品 — GET /api/product/search?keyword=xxx
export function searchProducts(keyword) {
  return request({
    url: '/product/search',
    method: 'get',
    params: { keyword }
  })
}

// 根据分类 ID 获取商品 — GET /api/product/category/id/{categoryId}
export function getProductsByCategory(categoryId) {
  return request({
    url: `/product/category/id/${categoryId}`,
    method: 'get'
  })
}

// 根据价格范围获取商品 — GET /api/product/price-range
export function getProductsByPriceRange(minPrice, maxPrice) {
  return request({
    url: '/product/price-range',
    method: 'get',
    params: { minPrice, maxPrice }
  })
}

// 发布商品 — POST /api/product/publish
export function publishProduct(data) {
  return request({
    url: '/product/publish',
    method: 'post',
    data
  })
}

// 更新商品状态 — PUT /api/product/status/{id}
export function updateProductStatus(id) {
  return request({
    url: `/product/status/${id}`,
    method: 'put'
  })
}

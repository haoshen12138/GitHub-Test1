// jypt_web/src/api/transaction.js
// ========== 交易（订单）相关 API ==========
import request from '@/utils/request'

// 创建交易 — POST /api/transaction/create
export function createTransaction(data) {
  return request({
    url: '/transaction/create',
    method: 'post',
    data
  })
}

// 根据ID获取交易详情 — GET /api/transaction/{id}
export function getTransactionById(id) {
  return request({
    url: `/transaction/${id}`,
    method: 'get'
  })
}

// 获取买家交易列表 — GET /api/transaction/buyer/{buyerId}
export function getBuyerTransactions(buyerId) {
  return request({
    url: `/transaction/buyer/${buyerId}`,
    method: 'get'
  })
}

// 获取卖家交易列表 — GET /api/transaction/seller/{sellerId}
export function getSellerTransactions(sellerId) {
  return request({
    url: `/transaction/seller/${sellerId}`,
    method: 'get'
  })
}

// 更新支付状态（付款）— PUT /api/transaction/payment-status/{id}
// body: { "paymentStatus": "已支付" }
export function updatePaymentStatus(id, paymentStatus) {
  return request({
    url: `/transaction/payment-status/${id}`,
    method: 'put',
    data: { paymentStatus }
  })
}

// 卖家发货 — PUT /api/transaction/shipping/{id}
// body: { "shippingTime": "2024-06-01T10:00:00", "sellerRemark": "已发顺丰" }
export function updateShippingInfo(id, shippingTime, sellerRemark) {
  return request({
    url: `/transaction/shipping/${id}`,
    method: 'put',
    data: { shippingTime, sellerRemark }
  })
}

// 买家确认收货 — PUT /api/transaction/receive/{id}
// body: { "receiveTime": "2024-06-03T15:00:00", "buyerRemark": "东西不错" }
export function updateReceiveInfo(id, receiveTime, buyerRemark) {
  return request({
    url: `/transaction/receive/${id}`,
    method: 'put',
    data: { receiveTime, buyerRemark }
  })
}

// 取消交易 — PUT /api/transaction/cancel/{id}
// body: { "cancelReason": "不想要了" }
export function cancelTransaction(id, cancelReason) {
  return request({
    url: `/transaction/cancel/${id}`,
    method: 'put',
    data: { cancelReason }
  })
}

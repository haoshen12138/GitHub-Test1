// jypt_web/src/api/user.js
import request from '@/utils/request'

// 用户登录 — POST /api/user/login
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册 — POST /api/user/register
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 根据 ID 获取用户信息 — GET /api/user/{id}
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// ========== 新增：修改密码 ==========
// PUT /api/user/change-password
// body: { userId, oldPassword, newPassword }
export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'put',
    data
  })
}

// ========== 新增：更新个人资料 ==========
// PUT /api/user/update
// body: { id, nickname, phone, email, avatar }
export function updateProfile(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

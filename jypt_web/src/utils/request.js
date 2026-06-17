// jypt_web/src/utils/request.js
// ========== 封装 axios，统一管理请求和响应 ==========
import axios from 'axios'

// 创建 axios 实例，baseURL 指向 /api（通过 vite proxy 转发到后端）
const service = axios.create({
  baseURL: '/api',
  timeout: 10000   // 10秒超时
})

// -------- 请求拦截器：在发送前可统一添加 token 等 --------
service.interceptors.request.use(
  config => {
    // 从 localStorage 读取登录时保存的用户信息
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// -------- 响应拦截器：统一处理后端返回的 Result 格式 --------
service.interceptors.response.use(
  response => {
    const res = response.data
    // 后端统一返回 { code: 200, message: 'xxx', data: ... }
    if (res.code === 200) {
      return res   // 成功时直接返回整个结果对象
    } else {
      // 业务错误（如用户名已存在）
      // 如果请求配置了 silentError，则不弹窗
      if (!response.config.silentError) {
        alert(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    // 网络错误或 HTTP 错误
    console.error('响应错误:', error)
    if (error.code === 'ECONNABORTED') {
      alert('请求超时，请检查网络连接')
    } else if (error.response?.status === 500) {
      alert('服务器内部错误')
    } else {
      alert('网络错误，请检查后端服务是否启动')
    }
    return Promise.reject(error)
  }
)

export default service

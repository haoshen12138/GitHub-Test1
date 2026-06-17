<!-- jypt_web/src/views/Login.vue -->
<!-- ========== 登录页面：支持登录和注册两种模式 ========== -->
<template>
  <div class="login-page">
    <!-- 左侧装饰区 -->
    <div class="login-left">
      <div class="brand">
        <h1>🎓 校园二手交易平台</h1>
        <p>让闲置物品找到新主人</p>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-right">
      <div class="form-card">
        <!-- 模式切换标签 -->
        <div class="tab-switch">
          <span :class="{ active: !isRegister }" @click="isRegister = false">登录</span>
          <span :class="{ active: isRegister }" @click="isRegister = true">注册</span>
        </div>

        <!-- 登录表单 -->
        <form v-if="!isRegister" @submit.prevent="handleLogin">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="loginForm.username" type="text" placeholder="请输入用户名" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="loginForm.password" type="password" placeholder="请输入密码" required />
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
          <p class="hint">提示：测试账号 zhangsan / 123456</p>
        </form>

        <!-- 注册表单 -->
        <form v-else @submit.prevent="handleRegister">
          <div class="form-group">
            <label>用户名 *</label>
            <input v-model="registerForm.username" type="text" placeholder="请输入用户名" required />
          </div>
          <div class="form-group">
            <label>密码 *</label>
            <input v-model="registerForm.password" type="password" placeholder="请输入密码" required />
          </div>
          <div class="form-group">
            <label>昵称</label>
            <input v-model="registerForm.nickname" type="text" placeholder="请输入昵称（选填）" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="registerForm.email" type="email" placeholder="请输入邮箱（选填）" />
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '注册中...' : '注 册' }}
          </button>
        </form>

        <!-- 提示信息 -->
        <p v-if="message" :class="messageType">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
// ========== 登录注册逻辑 ==========
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/user'

const router = useRouter()

// 当前模式：false=登录, true=注册
const isRegister = ref(false)
const loading = ref(false)
const message = ref('')
const messageType = ref('success-msg')

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

// -------- 处理登录 --------
async function handleLogin() {
  loading.value = true
  message.value = ''
  try {
    // 调用后端 /api/user/login 接口
    const res = await login({
      username: loginForm.username,
      password: loginForm.password
    })
    // 登录成功：保存用户信息到本地存储
    localStorage.setItem('user', JSON.stringify(res.data))
    localStorage.setItem('token', 'vue-jypt-token')
    message.value = '登录成功，正在跳转...'
    messageType.value = 'success-msg'
    // 延迟跳转到主页
    setTimeout(() => router.push('/main'), 800)
  } catch (err) {
    message.value = '登录失败：' + (err.message || '请检查账号密码')
    messageType.value = 'error-msg'
  } finally {
    loading.value = false
  }
}

// -------- 处理注册 --------
async function handleRegister() {
  loading.value = true
  message.value = ''
  try {
    // 调用后端 /api/user/register 接口
    await register({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || registerForm.username,
      email: registerForm.email || ''
    })
    message.value = '注册成功！请切换到登录标签进行登录'
    messageType.value = 'success-msg'
    // 清空注册表单
    registerForm.username = ''
    registerForm.password = ''
    registerForm.nickname = ''
    registerForm.email = ''
  } catch (err) {
    message.value = '注册失败：' + (err.message || '请重试')
    messageType.value = 'error-msg'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ========== 登录页样式 ========== */
.login-page {
  display: flex;
  min-height: 100vh;
}

/* 左侧品牌区 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand h1 {
  font-size: 2.4rem;
  margin-bottom: 10px;
}

.brand p {
  font-size: 1.1rem;
  opacity: 0.85;
}

/* 右侧表单区 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.form-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  width: 380px;
}

/* 登录/注册切换标签 */
.tab-switch {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 2px solid #eee;
}

.tab-switch span {
  flex: 1;
  text-align: center;
  padding: 10px;
  cursor: pointer;
  color: #999;
  font-size: 1.1rem;
  transition: 0.3s;
}

.tab-switch span.active {
  color: #667eea;
  border-bottom: 2px solid #667eea;
  margin-bottom: -2px;
  font-weight: bold;
}

/* 表单元素 */
.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 0.9rem;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 10px;
  transition: opacity 0.3s;
}

.submit-btn:hover {
  opacity: 0.9;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 提示文字 */
.hint {
  text-align: center;
  color: #999;
  font-size: 0.8rem;
  margin-top: 14px;
}

.success-msg {
  text-align: center;
  color: #67c23a;
  margin-top: 12px;
}

.error-msg {
  text-align: center;
  color: #f56c6c;
  margin-top: 12px;
}
</style>

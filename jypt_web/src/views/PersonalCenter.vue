<!-- jypt_web/src/views/PersonalCenter.vue -->
<!-- ========== 个人中心：展示信息 + 编辑资料 + 修改密码 ========== -->
<template>
  <div class="profile-page">
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/main')">← 返回首页</button>
      <h2>个人中心</h2>
      <div></div>
    </header>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <span :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">个人信息</span>
      <span :class="{ active: activeTab === 'edit' }" @click="activeTab = 'edit'">编辑资料</span>
      <span :class="{ active: activeTab === 'pwd' }" @click="activeTab = 'pwd'">修改密码</span>
    </div>

    <div class="content-area">
      <!-- ===== Tab1: 个人信息展示 ===== -->
      <div v-if="activeTab === 'info'" class="info-panel">
        <div class="avatar-area">
          <div class="avatar-circle">👤</div>
          <h3>{{ user.nickname || user.username }}</h3>
        </div>
        <div class="info-rows">
          <div class="info-row">
            <span class="label">用户ID</span><span class="value">{{ user.id }}</span>
          </div>
          <div class="info-row">
            <span class="label">用户名</span><span class="value">{{ user.username }}</span>
          </div>
          <div class="info-row">
            <span class="label">昵称</span><span class="value">{{ user.nickname || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="label">手机号</span><span class="value">{{ user.phone || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="label">邮箱</span><span class="value">{{ user.email || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="label">注册时间</span><span class="value">{{ formatTime(user.createTime) }}</span>
          </div>
        </div>
      </div>

      <!-- ===== Tab2: 编辑资料 ===== -->
      <div v-if="activeTab === 'edit'" class="edit-panel">
        <div class="form-group">
          <label>昵称</label>
          <input v-model="editForm.nickname" type="text" placeholder="请输入昵称" />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input v-model="editForm.phone" type="text" placeholder="请输入手机号" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="editForm.email" type="email" placeholder="请输入邮箱" />
        </div>
        <button class="submit-btn" :disabled="editLoading" @click="handleUpdateProfile">
          {{ editLoading ? '保存中...' : '保存修改' }}
        </button>
        <p v-if="editMsg" :class="editOk ? 'success-msg' : 'error-msg'">{{ editMsg }}</p>
      </div>

      <!-- ===== Tab3: 修改密码 ===== -->
      <div v-if="activeTab === 'pwd'" class="pwd-panel">
        <div class="form-group">
          <label>旧密码</label>
          <input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" />
        </div>
        <div class="form-group">
          <label>确认新密码</label>
          <input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" />
        </div>
        <button class="submit-btn" :disabled="pwdLoading" @click="handleChangePassword">
          {{ pwdLoading ? '修改中...' : '修改密码' }}
        </button>
        <p v-if="pwdMsg" :class="pwdOk ? 'success-msg' : 'error-msg'">{{ pwdMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getUserById, updateProfile, changePassword } from '@/api/user'

const activeTab = ref('info')

// 当前登录用户（从 localStorage 读取，同时从后端获取最新数据）
const user = ref({})
const currentUser = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} }
  catch { return {} }
})

// 编辑资料表单
const editForm = reactive({ nickname: '', phone: '', email: '' })
const editLoading = ref(false)
const editMsg = ref('')
const editOk = ref(false)

// 修改密码表单
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdLoading = ref(false)
const pwdMsg = ref('')
const pwdOk = ref(false)

// -------- 加载用户最新数据 --------
onMounted(async () => {
  const uid = currentUser.value.id
  if (!uid) return
  try {
    const res = await getUserById(uid)
    user.value = res.data
    // 同步填充编辑表单
    editForm.nickname = res.data.nickname || ''
    editForm.phone = res.data.phone || ''
    editForm.email = res.data.email || ''
    // 更新 localStorage 中的用户数据
    localStorage.setItem('user', JSON.stringify(res.data))
  } catch (err) {
    // 降级使用 localStorage 数据
    user.value = currentUser.value
    editForm.nickname = currentUser.value.nickname || ''
    editForm.phone = currentUser.value.phone || ''
    editForm.email = currentUser.value.email || ''
  }
})

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

// -------- 保存个人资料 --------
async function handleUpdateProfile() {
  editMsg.value = ''
  editLoading.value = true
  try {
    const res = await updateProfile({
      id: user.value.id,
      nickname: editForm.nickname || null,
      phone: editForm.phone || null,
      email: editForm.email || null
    })
    editOk.value = true
    editMsg.value = '保存成功！'
    // 更新页面显示和 localStorage
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
  } catch (err) {
    editOk.value = false
    editMsg.value = '保存失败：' + (err.message || '请重试')
  } finally {
    editLoading.value = false
  }
}

// -------- 修改密码 --------
async function handleChangePassword() {
  pwdMsg.value = ''
  // 前端校验
  if (!pwdForm.oldPassword) {
    pwdMsg.value = '请输入旧密码'
    return
  }
  if (!pwdForm.newPassword || pwdForm.newPassword.length < 6) {
    pwdMsg.value = '新密码至少6位'
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    pwdMsg.value = '两次输入的新密码不一致'
    return
  }
  pwdLoading.value = true
  try {
    await changePassword({
      userId: String(user.value.id),
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    pwdOk.value = true
    pwdMsg.value = '密码修改成功！下次登录请使用新密码'
    // 清空表单
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (err) {
    pwdOk.value = false
    pwdMsg.value = '修改失败：' + (err.message || '请重试')
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #f5f7fa; }

.top-nav {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 30px; background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.top-nav h2 { margin: 0; font-size: 1.1rem; }
.back-btn { padding: 6px 14px; background: #eee; border: none; border-radius: 4px; cursor: pointer; font-size: 0.9rem; }

/* Tab */
.tab-bar {
  display: flex; background: #fff; border-bottom: 2px solid #eee; padding: 0 30px;
}
.tab-bar span {
  padding: 14px 24px; cursor: pointer; color: #999; font-size: 1rem;
  border-bottom: 2px solid transparent; margin-bottom: -2px; transition: 0.3s;
}
.tab-bar span.active {
  color: #667eea; border-bottom-color: #667eea; font-weight: bold;
}

.content-area { max-width: 500px; margin: 24px auto; padding: 0 20px; }

/* 信息展示 */
.info-panel { background: #fff; border-radius: 8px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.avatar-area { text-align: center; margin-bottom: 24px; }
.avatar-circle {
  width: 72px; height: 72px; border-radius: 50%; background: #e8edff;
  display: flex; align-items: center; justify-content: center;
  font-size: 2.2rem; margin: 0 auto 10px;
}
.avatar-area h3 { margin: 0; font-size: 1.2rem; color: #333; }
.info-rows { }
.info-row {
  display: flex; justify-content: space-between; padding: 12px 0;
  border-bottom: 1px solid #f5f5f5; font-size: 0.9rem;
}
.info-row:last-child { border: none; }
.label { color: #999; }
.value { color: #333; font-weight: 500; }

/* 表单 */
.edit-panel, .pwd-panel {
  background: #fff; border-radius: 8px; padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.form-group { margin-bottom: 18px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 0.9rem; color: #555; }
.form-group input {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px;
  font-size: 0.95rem; box-sizing: border-box; transition: border-color 0.3s;
}
.form-group input:focus { outline: none; border-color: #667eea; }

.submit-btn {
  width: 100%; padding: 12px; background: linear-gradient(135deg,#667eea,#764ba2);
  color: #fff; border: none; border-radius: 6px; font-size: 1rem;
  cursor: pointer; transition: opacity 0.3s; margin-top: 6px;
}
.submit-btn:hover { opacity: 0.9; }
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.success-msg { color: #67c23a; text-align: center; margin-top: 12px; font-size: 0.9rem; }
.error-msg { color: #f56c6c; text-align: center; margin-top: 12px; font-size: 0.9rem; }
</style>

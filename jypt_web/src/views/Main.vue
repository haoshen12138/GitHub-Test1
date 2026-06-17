<!-- jypt_web/src/views/Main.vue -->
<!-- ========== 主页：顶部导航 + 商品展示区 ========== -->
<template>
  <div class="main-page">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <h2>🎓 校园二手交易</h2>
      </div>
      <!-- 搜索栏：输入关键词后回车跳转到搜索结果页 -->
      <div class="nav-center">
        <input
            v-model="keyword"
            type="text"
            placeholder="搜索商品..."
            @keyup.enter="goSearch"
        />
        <button @click="goSearch">🔍 搜索</button>
      </div>
            <div class="nav-right">
        <button class="publish-btn" @click="$router.push('/publish')">📢 发布商品</button>
        <button class="cart-btn" @click="$router.push('/cart')">🛒 购物车</button>
        <button class="profile-btn" @click="$router.push('/profile')">👤 个人中心</button>
        <button class="orders-btn" @click="$router.push('/orders')">📋 我的订单</button>
        <span class="user-name">{{ userName }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>

    </header>

    <!-- 主体内容区 -->
    <div class="main-body">
      <!-- 左侧分类栏 -->
      <aside class="sidebar">
        <h3>商品分类</h3>
        <ul>
          <li
              v-for="cat in categories"
              :key="cat"
              :class="{ active: activeCategory === cat }"
              @click="filterByCategory(cat)"
          >
            {{ cat }}
          </li>
          <li :class="{ active: activeCategory === '' }" @click="filterByCategory('')">
            全部商品
          </li>
        </ul>
      </aside>

      <!-- 右侧商品展示区 -->
      <main class="product-area">
        <h3>{{ activeCategory || '全部商品' }}</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else class="product-grid">
          <div
              v-for="product in displayProducts"
              :key="product.id"
              class="product-card"
              @click="goDetail(product.id)"
          >
            <div class="card-img">
              <img
                  :src="getFirstImage(product.images)"
                  :alt="product.title"
                  @error="e => e.target.src = 'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>📦</text></svg>'"
              />
            </div>
            <div class="card-info">
              <h4>{{ product.title }}</h4>
              <p class="card-desc">{{ product.description?.substring(0, 40) }}...</p>
              <div class="card-bottom">
                <span class="price">¥{{ product.price }}</span>
                <span class="status" :class="product.status === '出售中' ? 'on-sale' : 'sold'">
                  {{ product.status }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!loading && displayProducts.length === 0" class="empty-tip">
          暂无商品数据
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
// ========== 主页逻辑 ==========
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllProducts } from '@/api/product'

const router = useRouter()

const userName = ref('')
const keyword = ref('')
const activeCategory = ref('')
const allProducts = ref([])
const loading = ref(true)

const categories = computed(() => {
  const cats = allProducts.value.map(p => p.category).filter(Boolean)
  return [...new Set(cats)]
})

const displayProducts = computed(() => {
  if (!activeCategory.value) return allProducts.value
  return allProducts.value.filter(p => p.category === activeCategory.value)
})

onMounted(async () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      userName.value = user.nickname || user.username || '用户'
    } catch { /* ignore */ }
  }
  try {
    const res = await getAllProducts()
    allProducts.value = res.data || []
  } catch (err) {
    console.error('加载商品失败:', err)
  } finally {
    loading.value = false
  }
})

function getFirstImage(images) {
  if (!images) return ''
  return images.split(',')[0]?.trim() || ''
}

function filterByCategory(cat) { activeCategory.value = cat }

function goSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  router.push({ path: '/search', query: { keyword: kw } })
}

function goDetail(id) { router.push(`/product/${id}`) }

function handleLogout() {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
/* ========== 主页样式 ========== */
.main-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 30px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left h2 {
  margin: 0;
  color: #667eea;
  font-size: 1.3rem;
}

.nav-center {
  display: flex;
  gap: 0;
}

.nav-center input {
  width: 300px;
  padding: 8px 14px;
  border: 2px solid #ddd;
  border-right: none;
  border-radius: 6px 0 0 6px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.3s;
}

.nav-center input:focus {
  border-color: #667eea;
}

.nav-center button {
  padding: 8px 16px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  font-size: 0.9rem;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-btn {
  padding: 6px 14px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
}

.profile-btn:hover {
  opacity: 0.85;
}

.orders-btn {
  padding: 6px 14px;
  background: #e6a23c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
}

.orders-btn:hover {
  opacity: 0.85;
}

/* ========== 在这里插入购物车按钮样式 ========== */
.publish-btn {
  padding: 6px 14px;
  background: #67c23a;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
}

.publish-btn:hover {
  opacity: 0.85;
}

.cart-btn {
  padding: 6px 14px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
}

.cart-btn:hover {
  opacity: 0.85;
}
/* ========== 插入结束 ========== */

.user-name {
  color: #333;
}

.logout-btn {
  padding: 6px 14px;
  background: #f56c6c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

/* 主体布局 */
.main-body {
  display: flex;
  max-width: 1200px;
  margin: 20px auto;
  gap: 20px;
  padding: 0 20px;
}

.sidebar {
  width: 180px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  align-self: flex-start;
}

.sidebar h3 {
  margin: 0 0 14px;
  font-size: 1rem;
  color: #333;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar li {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
  font-size: 0.9rem;
  transition: 0.2s;
}

.sidebar li:hover {
  background: #f0f2ff;
}

.sidebar li.active {
  background: #667eea;
  color: #fff;
  font-weight: bold;
}

.product-area {
  flex: 1;
}

.product-area h3 {
  margin: 0 0 16px;
  font-size: 1.1rem;
  color: #333;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #999;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.card-img {
  height: 160px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-info {
  padding: 12px;
}

.card-info h4 {
  margin: 0 0 6px;
  font-size: 0.95rem;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 0.8rem;
  color: #999;
  margin: 0 0 10px;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 1.1rem;
  font-weight: bold;
}

.status {
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 10px;
}

.on-sale {
  background: #e8f5e9;
  color: #67c23a;
}

.sold {
  background: #fef0f0;
  color: #f56c6c;
}

.empty-tip {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>

<!-- jypt_web/src/views/SearchResult.vue -->
<!-- ========== 搜索结果页：展示搜索栏 + 符合关键词的商品列表 ========== -->
<template>
  <div class="search-page">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/main')">← 返回首页</button>
      <!-- 搜索栏：可在此修改关键词重新搜索 -->
      <div class="search-bar">
        <input
            v-model="keyword"
            type="text"
            placeholder="搜索商品..."
            @keyup.enter="doSearch"
        />
        <button @click="doSearch">🔍 搜索</button>
      </div>
      <div></div>
    </header>

    <!-- 加载中 -->
    <div v-if="loading" class="loading">搜索中...</div>

    <!-- 搜索结果 -->
    <div v-else class="result-area">
      <div class="result-header">
        <h3>搜索 "{{ routeQuery }}" 的结果</h3>
        <span class="count">共找到 {{ products.length }} 件商品</span>
      </div>

      <!-- 商品网格 -->
      <div v-if="products.length > 0" class="product-grid">
        <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goDetail(product.id)"
        >
          <div class="card-img">
            <img
                :src="getFirstImage(product.images)"
                :alt="product.title"
                @error="e => e.target.src = placeholderImg"
            />
          </div>
          <div class="card-info">
            <h4 v-html="highlight(product.title)"></h4>
            <p class="card-desc" v-html="highlight(product.description?.substring(0, 50))"></p>
            <div class="card-bottom">
              <span class="price">¥{{ product.price }}</span>
              <span class="status" :class="product.status === '出售中' ? 'on-sale' : 'sold'">
                {{ product.status }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 无结果 -->
      <div v-else class="empty-tip">
        <p>😔 没有找到相关商品</p>
        <p>试试其他关键词吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
// ========== 搜索结果页逻辑 ==========
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '@/api/product'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const routeQuery = ref('')     // 记录从URL获取的原始关键词
const products = ref([])
const loading = ref(true)

const placeholderImg = 'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>📦</text></svg>'

// -------- 页面加载时从URL获取关键词并搜索 --------
onMounted(() => {
  keyword.value = route.query.keyword || ''
  routeQuery.value = keyword.value
  if (keyword.value) {
    doSearch()
  } else {
    loading.value = false
  }
})

// -------- 执行搜索 --------
async function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  routeQuery.value = kw
  loading.value = true
  try {
    const res = await searchProducts(kw)
    products.value = res.data || []
  } catch (err) {
    console.error('搜索失败:', err)
    products.value = []
  } finally {
    loading.value = false
  }
}

// -------- 获取商品第一张图片 --------
function getFirstImage(images) {
  if (!images) return ''
  return images.split(',')[0]?.trim() || ''
}

// -------- 关键词高亮 --------
function highlight(text) {
  if (!text || !routeQuery.value) return text
  const regex = new RegExp(`(${routeQuery.value})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

// -------- 跳转商品详情 --------
function goDetail(id) {
  router.push(`/product/${id}`)
}
</script>

<style scoped>
/* ========== 搜索结果页样式 ========== */
.search-page {
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
}

.back-btn {
  padding: 6px 14px;
  background: #eee;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.search-bar {
  display: flex;
}

.search-bar input {
  width: 350px;
  padding: 8px 14px;
  border: 2px solid #ddd;
  border-right: none;
  border-radius: 6px 0 0 6px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.3s;
}

.search-bar input:focus {
  border-color: #667eea;
}

.search-bar button {
  padding: 8px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  font-size: 0.9rem;
}

.loading {
  text-align: center;
  padding: 80px;
  color: #999;
}

.result-area {
  max-width: 1100px;
  margin: 24px auto;
  padding: 0 20px;
}

.result-header {
  display: flex;
  align-items: baseline;
  gap: 14px;
  margin-bottom: 20px;
}

.result-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.count {
  color: #999;
  font-size: 0.9rem;
}

/* 商品网格 */
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

/* 关键词高亮 */
:deep(.highlight) {
  background: #fff3cd;
  color: #856404;
  font-weight: bold;
  padding: 1px 2px;
  border-radius: 2px;
}

.empty-tip {
  text-align: center;
  padding: 80px;
  color: #999;
}

.empty-tip p {
  margin: 4px 0;
}
</style>

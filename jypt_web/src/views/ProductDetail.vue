<!-- jypt_web/src/views/ProductDetail.vue -->
<!-- ========== 商品详情页：展示单个商品完整信息 + 下单购买弹窗 ========== -->
<template>
  <div class="detail-page">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h2>商品详情</h2>
      <div></div>
    </header>

    <!-- 加载中 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 商品详情内容 -->
    <div v-else-if="product" class="detail-body">
      <!-- 图片展示区 -->
      <div class="image-section">
        <img
            v-if="currentImage"
            :src="currentImage"
            :alt="product.title"
            class="main-image"
            @error="e => e.target.src = placeholderImg"
        />
        <div v-else class="no-image">📦 暂无图片</div>
        <!-- 图片缩略图列表 -->
        <div v-if="imageList.length > 1" class="thumb-list">
          <img
              v-for="(img, idx) in imageList"
              :key="idx"
              :src="img"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
              @error="e => e.target.style.display = 'none'"
          />
        </div>
      </div>

      <!-- 商品信息区 -->
      <div class="info-section">
        <h1>{{ product.title }}</h1>
        <div class="price-row">
          <span class="price">¥{{ product.price }}</span>
          <span class="status" :class="product.status === '出售中' ? 'on-sale' : 'sold'">
            {{ product.status }}
          </span>
        </div>
        <div class="meta">
          <span>分类：{{ product.category || '未分类' }}</span>
          <span>发布时间：{{ formatTime(product.createTime) }}</span>
          <span>卖家ID：{{ product.userId }}</span>
        </div>
        <div class="desc-section">
          <h3>商品描述</h3>
          <p>{{ product.description || '暂无描述' }}</p>
        </div>
        <!-- 购买按钮：仅出售中状态 + 非卖家本人 显示 -->
        <!-- 如果是自己的商品，不能购买自己 -->
        <button
            class="buy-btn"
            :disabled="product.status !== '出售中' || isSelfProduct"
            @click="showBuyDialog = true"
        >
          {{ isSelfProduct ? '自己的商品' : (product.status === '出售中' ? '立即购买' : '已售出') }}
        </button>
        <!-- ===== 在这里新增加入购物车按钮 ===== -->
        <button
            class="cart-btn"
            :disabled="product.status !== '出售中' || isSelfProduct || cartAdding"
            @click="handleAddToCart"
        >
          {{ cartAdding ? '添加中...' : (cartAdded ? '已加入购物车 ✓' : '加入购物车 🛒') }}
        </button>
        <!-- ===== 新增结束 ===== -->
        </div>    <!-- 关闭 info-section -->
    </div>      <!-- 关闭 v-else-if="product" -->

    <!-- 商品不存在 -->
    <div v-else class="not-found">

      <p>商品不存在或已被删除</p>
      <button @click="$router.push('/main')">返回首页</button>
    </div>

    <!-- ========== 购买弹窗（遮罩层 + 表单） ========== -->
    <div v-if="showBuyDialog" class="dialog-overlay" @click.self="showBuyDialog = false">
      <div class="buy-dialog">
        <h3>📦 确认下单</h3>
        <div class="dialog-product">
          <span class="dialog-title">{{ product.title }}</span>
          <span class="dialog-price">¥{{ product.price }}</span>
        </div>
        <!-- 交易地点 -->
        <div class="form-group">
          <label>交易地点 *</label>
          <input v-model="buyForm.tradingLocation" type="text" placeholder="如图书馆、食堂门口等" />
        </div>
        <!-- 买家备注 -->
        <div class="form-group">
          <label>备注（选填）</label>
          <textarea v-model="buyForm.buyerRemark" rows="2" placeholder="如：能便宜点吗、什么时候方便交易"></textarea>
        </div>
        <!-- 操作按钮 -->
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showBuyDialog = false">取消</button>
          <button class="confirm-btn" :disabled="submitting" @click="submitOrder">
            {{ submitting ? '提交中...' : '确认下单' }}
          </button>
        </div>
        <p v-if="buyError" class="error-msg">{{ buyError }}</p>
        <p v-if="buySuccess" class="success-msg">{{ buySuccess }}</p>
      </div>
    </div>
  </div>    <!-- ← 加这一行，关闭 detail-page -->
</template>


<script setup>
// ========== 商品详情逻辑 ==========
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductById } from '@/api/product'
import { addToCart } from '@/api/cart'
import { getUserById } from '@/api/user'
import { createTransaction } from '@/api/transaction'


const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(true)
const currentImage = ref('')
const placeholderImg = 'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>📦</text></svg>'

// 购买弹窗相关
const showBuyDialog = ref(false)
const submitting = ref(false)
const buyError = ref('')
const buySuccess = ref('')
const buyForm = reactive({
  tradingLocation: '',
  buyerRemark: ''
})
// 加入购物车相关      // ← 新增
const cartAdding = ref(false)    // ← 新增
const cartAdded = ref(false)     // ← 新增

// 获取当前登录用户
const currentUser = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch { return {} }
})

// 是否是自己的商品（自己的商品不能购买）
const isSelfProduct = computed(() => {
  return currentUser.value.id && currentUser.value.id === product.value?.userId
})

// 解析逗号分隔的图片列表
const imageList = computed(() => {
  if (!product.value?.images) return []
  return product.value.images.split(',').map(s => s.trim()).filter(Boolean)
})

// -------- 加载商品详情 --------
onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getProductById(id)
    product.value = res.data
    currentImage.value = imageList.value[0] || ''
  } catch (err) {
    console.error('加载商品详情失败:', err)
    product.value = null
  } finally {
    loading.value = false
  }
})

// -------- 格式化时间 --------
function formatTime(timeStr) {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

// -------- 提交订单：调用后端创建交易 --------
async function submitOrder() {
  buyError.value = ''
  buySuccess.value = ''

  if (!buyForm.tradingLocation.trim()) {
    buyError.value = '请填写交易地点'
    return
  }
  submitting.value = true

  try {
    // 获取卖家用户名（通过 userId 查询）
    let sellerName = ''
    try {
      const sellerRes = await getUserById(product.value.userId)
      // sellerRes.data 是 User 对象
      sellerName = sellerRes.data?.username || ''
    } catch { /* 获取失败不影响主流程 */ }

    // 构建交易对象
    const buyer = currentUser.value
    const transactionData = {
      productId: product.value.id,
      productTitle: product.value.title,
      price: product.value.price,
      sellerId: product.value.userId,
      sellerName: sellerName,
      buyerId: buyer.id,
      buyerName: buyer.username || buyer.nickname || '',
      tradingLocation: buyForm.tradingLocation.trim(),
      buyerRemark: buyForm.buyerRemark.trim() || null
    }

    const res = await createTransaction(transactionData)
    buySuccess.value = `下单成功！交易编号：${res.data.transactionNo}`

    // 1.5 秒后跳转到订单页
    setTimeout(() => {
      showBuyDialog.value = false
      router.push('/orders')
    }, 1500)
  } catch (err) {
    buyError.value = '下单失败：' + (err.message || '请稍后重试')
  } finally {
    submitting.value = false
  }
}

// -------- 加入购物车 --------            // ← 从这里开始新增
async function handleAddToCart() {
  cartAdding.value = true
  try {
    let sellerName = ''
    try {
      const sellerRes = await getUserById(product.value.userId)
      sellerName = sellerRes.data?.username || ''
    } catch { /* 获取失败不影响 */ }

    const firstImage = product.value.images?.split(',')[0]?.trim() || ''

    await addToCart({
      userId: currentUser.value.id,
      productId: product.value.id,
      productTitle: product.value.title,
      price: product.value.price,
      productImage: firstImage,
      sellerId: product.value.userId,
      sellerName: sellerName
    })

    cartAdded.value = true
  } catch (err) {
    alert('加入购物车失败：' + (err.response?.data?.message || err.message || '请重试'))
  } finally {
    cartAdding.value = false
  }
}
// ← 新增到这里结束

</script>


<style scoped>
/* ========== 商品详情页样式 ========== */
.detail-page {
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

.top-nav h2 {
  margin: 0;
  font-size: 1.1rem;
}

.back-btn {
  padding: 6px 14px;
  background: #eee;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.loading, .not-found {
  text-align: center;
  padding: 80px 0;
  color: #999;
}

.not-found button {
  margin-top: 16px;
  padding: 10px 24px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

/* 详情主体布局 */
.detail-body {
  display: flex;
  max-width: 1000px;
  margin: 30px auto;
  gap: 30px;
  padding: 0 20px;
}

/* 图片区 */
.image-section {
  flex: 1;
}

.main-image {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.no-image {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  font-size: 3rem;
  color: #ccc;
}

.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.thumb-list img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.thumb-list img.active {
  border-color: #667eea;
}

/* 信息区 */
.info-section {
  flex: 1;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-section h1 {
  margin: 0 0 16px;
  font-size: 1.4rem;
  color: #333;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.price {
  font-size: 1.8rem;
  color: #f56c6c;
  font-weight: bold;
}

.status {
  font-size: 0.85rem;
  padding: 3px 10px;
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

.meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: #999;
  font-size: 0.85rem;
  margin-bottom: 20px;
}

.desc-section {
  border-top: 1px solid #eee;
  padding-top: 16px;
  margin-bottom: 24px;
}

.desc-section h3 {
  margin: 0 0 10px;
  font-size: 1rem;
  color: #333;
}

.desc-section p {
  color: #666;
  line-height: 1.7;
  font-size: 0.9rem;
}

/* 购买按钮 */
.buy-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #f56c6c, #e63946);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: opacity 0.3s;
}

.buy-btn:hover {
  opacity: 0.9;
}

.buy-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 加入购物车按钮 */                       /* ← 新增 */
.cart-btn {                               /* ← 新增 */
  width: 100%;                            /* ← 新增 */
  padding: 12px;                          /* ← 新增 */
  background: linear-gradient(135deg, #667eea, #764ba2);  /* ← 新增 */
  color: #fff;                            /* ← 新增 */
  border: none;                           /* ← 新增 */
  border-radius: 8px;                     /* ← 新增 */
  font-size: 1rem;                        /* ← 新增 */
  cursor: pointer;                        /* ← 新增 */
  transition: opacity 0.3s;               /* ← 新增 */
  margin-top: 12px;                       /* ← 新增 */
}                                         /* ← 新增 */
                                          /* ← 新增 */
.cart-btn:hover:not(:disabled) {          /* ← 新增 */
  opacity: 0.9;                           /* ← 新增 */
}                                         /* ← 新增 */
                                          /* ← 新增 */
.cart-btn:disabled {                      /* ← 新增 */
  background: #ccc;                       /* ← 新增 */
  cursor: not-allowed;                    /* ← 新增 */
}                                         /* ← 新增 */

/* ========== 购买弹窗样式 ========== */


/* ========== 购买弹窗样式 ========== */
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.buy-dialog {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  width: 420px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.buy-dialog h3 {
  margin: 0 0 16px;
  font-size: 1.2rem;
}

.dialog-product {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 18px;
}

.dialog-title {
  font-weight: bold;
  color: #333;
}

.dialog-price {
  color: #f56c6c;
  font-size: 1.2rem;
  font-weight: bold;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 0.9rem;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
  font-family: inherit;
  resize: vertical;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.cancel-btn {
  flex: 1;
  padding: 10px;
  background: #eee;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
}

.confirm-btn {
  flex: 2;
  padding: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: opacity 0.3s;
}

.confirm-btn:hover {
  opacity: 0.9;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-msg {
  color: #f56c6c;
  text-align: center;
  margin-top: 10px;
  font-size: 0.85rem;
}

.success-msg {
  color: #67c23a;
  text-align: center;
  margin-top: 10px;
  font-size: 0.85rem;
}
</style>

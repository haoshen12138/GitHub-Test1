<!-- jypt_web/src/views/Cart.vue -->
<!-- ========== 购物车页：商品列表 + 购买下单功能 ========== -->
<template>
  <div class="cart-page">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/main')">← 返回首页</button>
      <h2>我的购物车</h2>
      <div></div>
    </header>

    <!-- 加载中 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 空购物车提示 -->
    <div v-else-if="cartList.length === 0" class="empty-cart">
      <div class="empty-icon">🛒</div>
      <p>购物车是空的</p>
      <button class="go-shopping-btn" @click="$router.push('/main')">去逛逛</button>
    </div>

    <!-- 购物车列表 -->
    <div v-else class="cart-container">
      <!-- 操作栏 -->
      <div class="cart-actions">
        <button class="clear-btn" @click="handleClearCart">清空购物车</button>
        <span class="cart-count">共 {{ cartList.length }} 件商品</span>
      </div>

      <!-- 购物车商品列表 -->
      <div class="cart-list">
        <div
            v-for="item in cartList"
            :key="item.id"
            class="cart-item"
        >
          <!-- 已售出遮罩标识 -->
          <div v-if="item.productStatus === '已售出'" class="sold-out-badge">已售出</div>
          <!-- 商品图片 -->
          <div class="item-image" @click="goProductDetail(item.productId)">
            <img
                :src="item.productImage || getDefaultImage()"
                :alt="item.productTitle"
                @error="e => e.target.src = getDefaultImage()"
            />
          </div>

          <!-- 商品信息 -->
          <div class="item-info" @click="goProductDetail(item.productId)">
            <h3 class="item-title">{{ item.productTitle }}</h3>
            <p class="item-seller">卖家：{{ item.sellerName || 'ID:' + item.sellerId }}</p>
            <div class="item-price">¥{{ item.price }}</div>
          </div>

          <!-- 操作按钮 -->
          <div class="item-actions" @click.stop>
            <button
                class="buy-btn"
                :disabled="item.productStatus === '已售出'"
                @click="openBuyDialog(item)"
            >
              {{ item.productStatus === '已售出' ? '已售出' : '立即购买' }}
            </button>
            <button class="delete-btn" @click="handleDelete(item)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 下单弹窗 ========== -->
    <div v-if="showBuyDialog" class="dialog-overlay" @click.self="showBuyDialog = false">
      <div class="buy-dialog">
        <h3>📦 确认下单</h3>
        <div class="dialog-product">
          <span class="dialog-title">{{ buyingItem?.productTitle }}</span>
          <span class="dialog-price">¥{{ buyingItem?.price }}</span>
        </div>
        <div class="form-group">
          <label>交易地点 *</label>
          <input v-model="buyForm.tradingLocation" type="text" placeholder="如图书馆、食堂门口等" />
        </div>
        <div class="form-group">
          <label>备注（选填）</label>
          <textarea v-model="buyForm.buyerRemark" rows="2" placeholder="如：能便宜点吗、什么时候方便交易"></textarea>
        </div>
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

    <!-- ========== 删除/清空确认弹窗 ========== -->
    <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="action-dialog">
        <h3>{{ dialogTitle }}</h3>
        <p class="dialog-tip">{{ dialogTip }}</p>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="closeDialog">取消</button>
          <button class="confirm-btn" :disabled="submitting" @click="confirmAction">
            {{ submitting ? '处理中...' : '确认' }}
          </button>
        </div>
        <p v-if="dialogMsg" :class="dialogOk ? 'success-msg' : 'error-msg'">{{ dialogMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, deleteCartItem, clearCart } from '@/api/cart'
import { getProductById } from '@/api/product'
import { createTransaction } from '@/api/transaction'

const router = useRouter()

const cartList = ref([])
const loading = ref(true)

const currentUser = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} }
  catch { return {} }
})

const showBuyDialog = ref(false)
const submitting = ref(false)
const buyError = ref('')
const buySuccess = ref('')
const buyingItem = ref(null)
const buyForm = reactive({
  tradingLocation: '',
  buyerRemark: ''
})

const showDialog = ref(false)
const dialogTitle = ref('')
const dialogTip = ref('')
const dialogMsg = ref('')
const dialogOk = ref(false)
let currentAction = null
let currentItem = null

// 加载购物车数据，同时校验每个商品的状态
onMounted(async () => {
  const uid = currentUser.value.id
  if (!uid) {
    loading.value = false
    return
  }
  try {
    const res = await getCartList(uid)
    const items = res.data || []
    // 逐个查询商品实时状态
    for (const item of items) {
      try {
        const productRes = await getProductById(item.productId, true)
        item.productStatus = productRes.data?.status || '未知'
      } catch (err) {
        // 商品不存在或已删除，标记为已下架
        item.productStatus = '已下架'
      }
    }
    cartList.value = items
  } catch (err) {
    console.error('加载购物车失败:', err)
  } finally {
    loading.value = false
  }
})

function getDefaultImage() {
  return 'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>📦</text></svg>'
}

function goProductDetail(productId) {
  router.push(`/product/${productId}`)
}

function openBuyDialog(item) {
  if (item.productStatus === '已售出') {
    alert('该商品已被他人购买，无法下单')
    return
  }
  buyingItem.value = item
  buyForm.tradingLocation = ''
  buyForm.buyerRemark = ''
  buyError.value = ''
  buySuccess.value = ''
  showBuyDialog.value = true
}

function handleDelete(item) {
  currentAction = 'delete'
  currentItem = item
  dialogTitle.value = '删除商品'
  dialogTip.value = `确定要从购物车删除「${item.productTitle}」吗？`
  showDialog.value = true
}

function handleClearCart() {
  currentAction = 'clear'
  currentItem = null
  dialogTitle.value = '清空购物车'
  dialogTip.value = '确定要清空购物车中的所有商品吗？此操作不可恢复！'
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
  dialogMsg.value = ''
  dialogOk.value = false
  currentAction = null
  currentItem = null
}

async function submitOrder() {
  buyError.value = ''
  buySuccess.value = ''

  if (!buyForm.tradingLocation.trim()) {
    buyError.value = '请填写交易地点'
    return
  }
  submitting.value = true

  try {
    const buyer = currentUser.value
    const transactionData = {
      productId: buyingItem.value.productId,
      productTitle: buyingItem.value.productTitle,
      price: buyingItem.value.price,
      sellerId: buyingItem.value.sellerId,
      sellerName: buyingItem.value.sellerName,
      buyerId: buyer.id,
      buyerName: buyer.username || buyer.nickname || '',
      tradingLocation: buyForm.tradingLocation.trim(),
      buyerRemark: buyForm.buyerRemark.trim() || null
    }

    const res = await createTransaction(transactionData)
    buySuccess.value = `下单成功！交易编号：${res.data.transactionNo}`

    // 从购物车中删除已购买的商品
    await deleteCartItem(buyingItem.value.id)

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

async function confirmAction() {
  submitting.value = true
  dialogMsg.value = ''

  try {
    const uid = currentUser.value.id

    if (currentAction === 'delete') {
      await deleteCartItem(currentItem.id)
      dialogOk.value = true
      dialogMsg.value = '删除成功！'
      setTimeout(() => {
        closeDialog()
        location.reload()
      }, 800)
    } else if (currentAction === 'clear') {
      await clearCart(uid)
      dialogOk.value = true
      dialogMsg.value = '清空成功！'
      setTimeout(() => {
        closeDialog()
        location.reload()
      }, 800)
    }
  } catch (err) {
    dialogOk.value = false
    dialogMsg.value = '操作失败：' + (err.message || '请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
/* ========== 购物车页面样式 ========== */
.cart-page {
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
  color: #667eea;
}

.back-btn {
  padding: 6px 14px;
  background: #eee;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.back-btn:hover {
  opacity: 0.8;
}

.loading {
  text-align: center;
  padding: 80px;
  color: #999;
  font-size: 1rem;
}

.empty-cart {
  text-align: center;
  padding: 100px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-cart p {
  color: #999;
  font-size: 1.1rem;
  margin-bottom: 30px;
}

.go-shopping-btn {
  padding: 12px 40px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.go-shopping-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.cart-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.cart-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.clear-btn {
  padding: 6px 16px;
  background: #f56c6c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
}

.clear-btn:hover {
  opacity: 0.85;
}

.cart-count {
  color: #666;
  font-size: 0.9rem;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cart-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s, transform 0.2s;
  position: relative;
  overflow: hidden;
}

.cart-item:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 已售出商品样式 */
.item-sold-out {
  opacity: 0.65;
}

.sold-out-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #f56c6c;
  color: #fff;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: bold;
  z-index: 1;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f0f2f5;
  cursor: pointer;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  margin-left: 16px;
  cursor: pointer;
}

.item-title {
  margin: 0 0 8px;
  font-size: 1rem;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-seller {
  margin: 0 0 10px;
  font-size: 0.85rem;
  color: #999;
}

.item-price {
  font-size: 1.2rem;
  color: #f56c6c;
  font-weight: bold;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
}

.buy-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.buy-btn:hover:not(:disabled) {
  opacity: 0.85;
}

.buy-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.delete-btn {
  padding: 8px 20px;
  background: #f56c6c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.delete-btn:hover {
  opacity: 0.85;
}

/* ========== 下单弹窗样式 ========== */
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
  color: #333;
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
  font-size: 0.9rem;
  transition: opacity 0.2s;
}

.cancel-btn:hover {
  opacity: 0.8;
}

.confirm-btn {
  flex: 2;
  padding: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: opacity 0.3s;
}

.confirm-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.success-msg {
  color: #67c23a;
  text-align: center;
  margin-top: 10px;
  font-size: 0.85rem;
}

.error-msg {
  color: #f56c6c;
  text-align: center;
  margin-top: 10px;
  font-size: 0.85rem;
}

/* ========== 删除/清空确认弹窗样式 ========== */
.action-dialog {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  width: 380px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.action-dialog h3 {
  margin: 0 0 12px;
  font-size: 1.15rem;
  color: #333;
}

.dialog-tip {
  color: #666;
  margin-bottom: 20px;
  white-space: pre-line;
}
</style>

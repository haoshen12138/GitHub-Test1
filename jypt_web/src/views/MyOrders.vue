<!-- jypt_web/src/views/MyOrders.vue -->
<!-- ========== 我的订单页：买家/卖家双 Tab 交易列表 ========== -->
<template>
  <div class="orders-page">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/main')">← 返回首页</button>
      <h2>我的订单</h2>
      <div></div>
    </header>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <span :class="{ active: activeTab === 'buyer' }" @click="switchTab('buyer')">
        我买的 ({{ buyerOrders.length }})
      </span>
      <span :class="{ active: activeTab === 'seller' }" @click="switchTab('seller')">
        我卖的 ({{ sellerOrders.length }})
      </span>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 订单列表 -->
    <div v-else class="order-list">
      <div v-if="currentOrders.length === 0" class="empty-tip">
        暂无相关订单
      </div>

      <!-- 单条订单卡片 -->
      <div
          v-for="order in currentOrders"
          :key="order.id"
          class="order-card"
          @click="goDetail(order.id)"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ order.transactionNo }}</span>
          <!-- 状态标签 -->
          <span class="order-status" :class="statusClass(order.status)">
            {{ order.status }}
          </span>
        </div>

        <!-- 订单主体 -->
        <div class="order-body">
          <div class="order-product">
            <span class="product-name">{{ order.productTitle }}</span>
            <span class="product-price">¥{{ order.price }}</span>
          </div>
          <div class="order-parties">
            <span>卖家：{{ order.sellerName || 'ID:' + order.sellerId }}</span>
            <span>买家：{{ order.buyerName || 'ID:' + order.buyerId }}</span>
          </div>
        </div>

        <!-- 订单时间 -->
        <div class="order-footer">
          <span>{{ formatTime(order.createTime) }}</span>
          <!-- 快捷操作按钮 -->
          <div class="quick-actions" @click.stop>
            <!-- 买家 + 待付款 → 付款 -->
            <button
                v-if="activeTab === 'buyer' && order.status === '待付款'"
                class="action-btn pay-btn"
                @click="handlePay(order)"
            >去付款</button>
            <!-- 买家 + 待收货 → 确认收货 -->
            <button
                v-if="activeTab === 'buyer' && order.status === '待收货'"
                class="action-btn receive-btn"
                @click="handleReceive(order)"
            >确认收货</button>
            <!-- 卖家 + 待发货 → 发货 -->
            <button
                v-if="activeTab === 'seller' && order.status === '待发货'"
                class="action-btn ship-btn"
                @click="handleShip(order)"
            >确认发货</button>
            <!-- 待付款状态 → 取消 -->
            <button
                v-if="order.status === '待付款'"
                class="action-btn cancel-btn"
                @click="handleCancel(order)"
            >取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 操作弹窗（付款/发货/收货/取消 共用） ========== -->
    <div v-if="showActionDialog" class="dialog-overlay" @click.self="showActionDialog = false">
      <div class="action-dialog">
        <h3>{{ actionTitle }}</h3>
        <p class="action-tip">{{ actionTip }}</p>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showActionDialog = false">关闭</button>
          <button class="confirm-btn" :disabled="actionSubmitting" @click="confirmAction">
            {{ actionSubmitting ? '处理中...' : '确认' }}
          </button>
        </div>
        <p v-if="actionMsg" :class="actionOk ? 'success-msg' : 'error-msg'">{{ actionMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBuyerTransactions, getSellerTransactions, updatePaymentStatus, updateShippingInfo, updateReceiveInfo, cancelTransaction } from '@/api/transaction'

const router = useRouter()

const activeTab = ref('buyer')
const buyerOrders = ref([])
const sellerOrders = ref([])
const loading = ref(true)

// 当前登录用户
const currentUser = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} }
  catch { return {} }
})

// 当前Tab的订单列表
const currentOrders = computed(() =>
  activeTab.value === 'buyer' ? buyerOrders.value : sellerOrders.value
)

// -------- 弹窗相关 --------
const showActionDialog = ref(false)
const actionTitle = ref('')
const actionTip = ref('')
const actionSubmitting = ref(false)
const actionMsg = ref('')
const actionOk = ref(false)
let currentAction = null       // 当前操作类型
let currentOrder = null        // 当前操作的订单

// -------- 加载数据 --------
onMounted(async () => {
  const uid = currentUser.value.id
  if (!uid) {
    loading.value = false
    return
  }
  try {
    const [buyerRes, sellerRes] = await Promise.all([
      getBuyerTransactions(uid),
      getSellerTransactions(uid)
    ])
    buyerOrders.value = buyerRes.data || []
    sellerOrders.value = sellerRes.data || []
  } catch (err) {
    console.error('加载订单失败:', err)
  } finally {
    loading.value = false
  }
})

// -------- 切换Tab --------
function switchTab(tab) {
  activeTab.value = tab
}

// -------- 格式化时间 --------
function formatTime(timeStr) {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

// -------- 状态样式class --------
function statusClass(status) {
  const map = {
    '待付款': 's-pending-pay',
    '待发货': 's-pending-ship',
    '待收货': 's-pending-receive',
    '已完成': 's-done',
    '已取消': 's-cancel'
  }
  return map[status] || ''
}

// -------- 跳转详情 --------
function goDetail(id) {
  router.push(`/transaction/${id}`)
}

// ========== 快捷操作 ==========

// 付款
function handlePay(order) {
  currentOrder = order
  currentAction = 'pay'
  actionTitle.value = '确认付款'
  actionTip.value = `确认支付 ¥${order.price} ？`
  showActionDialog.value = true
}

// 发货
function handleShip(order) {
  currentOrder = order
  currentAction = 'ship'
  actionTitle.value = '确认发货'
  actionTip.value = `确认为订单「${order.productTitle}」发货？`
  showActionDialog.value = true
}

// 收货
function handleReceive(order) {
  currentOrder = order
  currentAction = 'receive'
  actionTitle.value = '确认收货'
  actionTip.value = `确认已收到「${order.productTitle}」？`
  showActionDialog.value = true
}

// 取消
function handleCancel(order) {
  currentOrder = order
  currentAction = 'cancel'
  actionTitle.value = '取消交易'
  actionTip.value = `确定要取消订单「${order.productTitle}」吗？`
  showActionDialog.value = true
}

// -------- 确认操作 --------
async function confirmAction() {
  actionSubmitting.value = true
  actionMsg.value = ''
  try {
    const now = new Date().toISOString().replace('Z', '').substring(0, 19)
    if (currentAction === 'pay') {
      await updatePaymentStatus(currentOrder.id, '已支付')
    } else if (currentAction === 'ship') {
      await updateShippingInfo(currentOrder.id, now, null)
    } else if (currentAction === 'receive') {
      await updateReceiveInfo(currentOrder.id, now, null)
    } else if (currentAction === 'cancel') {
      await cancelTransaction(currentOrder.id, '用户主动取消')
    }
    actionOk.value = true
    actionMsg.value = '操作成功！'
    // 刷新列表
    setTimeout(() => {
      showActionDialog.value = false
      location.reload()
    }, 800)
  } catch (err) {
    actionOk.value = false
    actionMsg.value = '操作失败：' + (err.message || '请重试')
  } finally {
    actionSubmitting.value = false
  }
}
</script>

<style scoped>
.orders-page {
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

.top-nav h2 { margin: 0; font-size: 1.1rem; }
.back-btn { padding: 6px 14px; background: #eee; border: none; border-radius: 4px; cursor: pointer; }

/* Tab 切换 */
.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 2px solid #eee;
  padding: 0 30px;
}

.tab-bar span {
  padding: 14px 24px;
  cursor: pointer;
  color: #999;
  font-size: 1rem;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: 0.3s;
}

.tab-bar span.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: bold;
}

.loading, .empty-tip {
  text-align: center;
  padding: 60px;
  color: #999;
}

/* 订单列表 */
.order-list {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

/* 订单卡片 */
.order-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 18px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no { font-size: 0.8rem; color: #999; }

/* 状态标签 */
.order-status {
  font-size: 0.8rem;
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: bold;
}

.s-pending-pay { background: #fef0f0; color: #f56c6c; }
.s-pending-ship { background: #fdf6ec; color: #e6a23c; }
.s-pending-receive { background: #ecf5ff; color: #409eff; }
.s-done { background: #e8f5e9; color: #67c23a; }
.s-cancel { background: #f4f4f5; color: #909399; }

.order-body {
  padding: 14px 18px;
}

.order-product {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.product-name { font-weight: bold; color: #333; }
.product-price { color: #f56c6c; font-size: 1.1rem; font-weight: bold; }

.order-parties {
  display: flex;
  gap: 24px;
  font-size: 0.85rem;
  color: #666;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 18px;
  border-top: 1px solid #f0f0f0;
  font-size: 0.8rem;
  color: #999;
}

/* 快捷操作按钮 */
.quick-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
  color: #fff;
  transition: opacity 0.2s;
}

.action-btn:hover { opacity: 0.85; }

.pay-btn { background: #f56c6c; }
.ship-btn { background: #e6a23c; }
.receive-btn { background: #409eff; }
.cancel-btn { background: #909399; }

/* ========== 弹窗样式 ========== */
.dialog-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 200;
}

.action-dialog {
  background: #fff; border-radius: 12px; padding: 30px;
  width: 380px; box-shadow: 0 8px 30px rgba(0,0,0,0.15);
}

.action-dialog h3 { margin: 0 0 12px; font-size: 1.15rem; }
.action-tip { color: #666; margin-bottom: 20px; }

.dialog-actions { display: flex; gap: 12px; }
.cancel-btn { flex:1; padding: 10px; background: #eee; border: none; border-radius: 6px; cursor: pointer; }
.confirm-btn { flex:2; padding: 10px; background: linear-gradient(135deg,#667eea,#764ba2); color:#fff; border:none; border-radius:6px; cursor:pointer; }
.confirm-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.success-msg { color: #67c23a; text-align: center; margin-top: 10px; }
.error-msg { color: #f56c6c; text-align: center; margin-top: 10px; }
</style>

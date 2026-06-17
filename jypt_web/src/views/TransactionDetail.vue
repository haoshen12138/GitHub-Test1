<!-- jypt_web/src/views/TransactionDetail.vue -->
<!-- ========== 交易详情页：完整交易信息 + 状态流转操作 ========== -->
<template>
  <div class="tx-detail-page">
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/orders')">← 返回订单</button>
      <h2>交易详情</h2>
      <div></div>
    </header>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="tx" class="detail-body">
      <!-- 状态时间线 -->
      <div class="timeline">
        <div class="tl-item" :class="{ done: isStatusDone('待付款') }">
          <div class="tl-dot"></div>
          <div class="tl-info">
            <span class="tl-title">下单</span>
            <span class="tl-time">{{ formatTime(tx.createTime) }}</span>
          </div>
        </div>
        <div class="tl-item" :class="{ done: isStatusDone('待发货') }">
          <div class="tl-dot"></div>
          <div class="tl-info">
            <span class="tl-title">付款</span>
            <span class="tl-time">{{ formatTime(tx.paymentTime) || '待付款' }}</span>
          </div>
        </div>
        <div class="tl-item" :class="{ done: isStatusDone('待收货') }">
          <div class="tl-dot"></div>
          <div class="tl-info">
            <span class="tl-title">发货</span>
            <span class="tl-time">{{ formatTime(tx.shippingTime) || '待发货' }}</span>
          </div>
        </div>
        <div class="tl-item" :class="{ done: isStatusDone('已完成') }">
          <div class="tl-dot"></div>
          <div class="tl-info">
            <span class="tl-title">收货完成</span>
            <span class="tl-time">{{ formatTime(tx.completeTime) || '待收货' }}</span>
          </div>
        </div>
      </div>

      <!-- 交易信息卡片 -->
      <div class="info-card">
        <div class="card-row">
          <span class="label">交易编号</span>
          <span class="value">{{ tx.transactionNo }}</span>
        </div>
        <div class="card-row">
          <span class="label">商品名称</span>
          <span class="value">{{ tx.productTitle }}</span>
        </div>
        <div class="card-row">
          <span class="label">交易金额</span>
          <span class="value price">¥{{ tx.price }}</span>
        </div>
        <div class="card-row">
          <span class="label">当前状态</span>
          <span class="value">
            <span class="badge" :class="statusClass(tx.status)">{{ tx.status }}</span>
          </span>
        </div>
        <div class="card-row">
          <span class="label">支付状态</span>
          <span class="value">{{ tx.paymentStatus }}</span>
        </div>
        <div class="card-row">
          <span class="label">交易地点</span>
          <span class="value">{{ tx.tradingLocation || '未指定' }}</span>
        </div>
        <div class="card-row">
          <span class="label">卖家</span>
          <span class="value">{{ tx.sellerName || 'ID:' + tx.sellerId }}</span>
        </div>
        <div class="card-row">
          <span class="label">买家</span>
          <span class="value">{{ tx.buyerName || 'ID:' + tx.buyerId }}</span>
        </div>
        <div class="card-row" v-if="tx.buyerRemark">
          <span class="label">买家备注</span>
          <span class="value">{{ tx.buyerRemark }}</span>
        </div>
        <div class="card-row" v-if="tx.sellerRemark">
          <span class="label">卖家备注</span>
          <span class="value">{{ tx.sellerRemark }}</span>
        </div>
        <div class="card-row" v-if="tx.cancelReason">
          <span class="label">取消原因</span>
          <span class="value cancel-reason">{{ tx.cancelReason }}</span>
        </div>
      </div>

      <!-- ========== 操作按钮区 ========== -->
      <div v-if="!isDone" class="action-bar">
        <!-- 买家 + 待付款 -->
        <template v-if="isBuyer && tx.status === '待付款'">
          <button class="act-btn primary" @click="doPay">💳 确认付款</button>
          <button class="act-btn danger" @click="doCancel">取消交易</button>
        </template>
        <!-- 卖家 + 待发货 -->
        <template v-if="isSeller && tx.status === '待发货'">
          <button class="act-btn primary" @click="doShip">📦 确认发货</button>
        </template>
        <!-- 买家 + 待收货 -->
        <template v-if="isBuyer && tx.status === '待收货'">
          <button class="act-btn primary" @click="doReceive">✅ 确认收货</button>
        </template>
      </div>
      <div v-else class="done-tip">此交易已结束</div>
    </div>

    <div v-else class="not-found">
      <p>交易不存在</p>
    </div>

    <!-- 确认弹窗 -->
    <div v-if="showDialog" class="dialog-overlay" @click.self="showDialog = false">
      <div class="confirm-dialog">
        <h3>{{ dialogTitle }}</h3>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="showDialog = false">取消</button>
          <button class="confirm-btn" :disabled="dialogLoading" @click="confirmDialogAction">
            {{ dialogLoading ? '处理中...' : '确定' }}
          </button>
        </div>
        <p v-if="dialogMsg" :class="dialogOk ? 'success-msg' : 'error-msg'">{{ dialogMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTransactionById, updatePaymentStatus, updateShippingInfo, updateReceiveInfo, cancelTransaction } from '@/api/transaction'

const route = useRoute()
const router = useRouter()

const tx = ref(null)
const loading = ref(true)

// 状态顺序映射
const statusOrder = { '待付款': 1, '待发货': 2, '待收货': 3, '已完成': 4, '已取消': 99 }
const currentStatusOrder = computed(() => statusOrder[tx.value?.status] || 0)

// 当前用户
const currentUser = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} }
  catch { return {} }
})
const isBuyer = computed(() => currentUser.value.id === tx.value?.buyerId)
const isSeller = computed(() => currentUser.value.id === tx.value?.sellerId)
const isDone = computed(() => tx.value?.status === '已完成' || tx.value?.status === '已取消')

// 弹窗
const showDialog = ref(false)
const dialogTitle = ref('')
const dialogLoading = ref(false)
const dialogMsg = ref('')
const dialogOk = ref(false)
let dialogAction = null

// -------- 加载 --------
onMounted(async () => {
  try {
    const res = await getTransactionById(route.params.id)
    tx.value = res.data
  } catch {
    tx.value = null
  } finally {
    loading.value = false
  }
})

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

function statusClass(s) {
  const m = { '待付款':'s-pay','待发货':'s-ship','待收货':'s-recv','已完成':'s-done','已取消':'s-cancel' }
  return m[s] || ''
}

function isStatusDone(status) {
  return (statusOrder[status] || 0) <= currentStatusOrder.value
}

// -------- 操作 --------
function doPay() {
  dialogTitle.value = '确认付款？'
  dialogAction = 'pay'
  showDialog.value = true
}
function doShip() {
  dialogTitle.value = '确认发货？'
  dialogAction = 'ship'
  showDialog.value = true
}
function doReceive() {
  dialogTitle.value = '确认收货？'
  dialogAction = 'receive'
  showDialog.value = true
}
function doCancel() {
  dialogTitle.value = '确定取消交易？'
  dialogAction = 'cancel'
  showDialog.value = true
}

async function confirmDialogAction() {
  dialogLoading.value = true
  dialogMsg.value = ''
  try {
    const now = new Date().toISOString().replace('Z', '').substring(0, 19)
    if (dialogAction === 'pay') {
      await updatePaymentStatus(tx.value.id, '已支付')
    } else if (dialogAction === 'ship') {
      await updateShippingInfo(tx.value.id, now, null)
    } else if (dialogAction === 'receive') {
      await updateReceiveInfo(tx.value.id, now, null)
    } else if (dialogAction === 'cancel') {
      await cancelTransaction(tx.value.id, '用户主动取消')
    }
    dialogOk.value = true
    dialogMsg.value = '操作成功！'
    setTimeout(() => location.reload(), 800)
  } catch (err) {
    dialogOk.value = false
    dialogMsg.value = '失败：' + (err.message || '请重试')
  } finally {
    dialogLoading.value = false
  }
}
</script>

<style scoped>
.tx-detail-page { min-height: 100vh; background: #f5f7fa; }
.top-nav { display: flex; align-items: center; justify-content: space-between; padding: 12px 30px; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.top-nav h2 { margin: 0; font-size: 1.1rem; }
.back-btn { padding: 6px 14px; background: #eee; border: none; border-radius: 4px; cursor: pointer; }
.loading, .not-found { text-align: center; padding: 80px; color: #999; }

.detail-body { max-width: 700px; margin: 24px auto; padding: 0 20px; }

/* 状态时间线 */
.timeline { display: flex; justify-content: space-between; background: #fff; border-radius: 8px; padding: 24px 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.tl-item { text-align: center; position: relative; flex: 1; }
.tl-item::after { content: ''; position: absolute; top: 10px; left: 55%; width: 90%; height: 2px; background: #ddd; z-index: 0; }
.tl-item:last-child::after { display: none; }
.tl-item.done::after { background: #67c23a; }
.tl-dot { width: 20px; height: 20px; border-radius: 50%; background: #ddd; margin: 0 auto 10px; position: relative; z-index: 1; }
.tl-item.done .tl-dot { background: #67c23a; }
.tl-title { display: block; font-size: 0.85rem; color: #999; }
.tl-item.done .tl-title { color: #67c23a; font-weight: bold; }
.tl-time { display: block; font-size: 0.7rem; color: #bbb; margin-top: 4px; }

/* 信息卡片 */
.info-card { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); margin-bottom: 20px; }
.card-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f5f5f5; font-size: 0.9rem; }
.card-row:last-child { border: none; }
.label { color: #999; }
.value { color: #333; }
.price { color: #f56c6c; font-weight: bold; font-size: 1.05rem; }
.cancel-reason { color: #f56c6c; }
.badge { font-size: 0.8rem; padding: 3px 10px; border-radius: 10px; font-weight: bold; }
.s-pay { background: #fef0f0; color: #f56c6c; }
.s-ship { background: #fdf6ec; color: #e6a23c; }
.s-recv { background: #ecf5ff; color: #409eff; }
.s-done { background: #e8f5e9; color: #67c23a; }
.s-cancel { background: #f4f4f5; color: #909399; }

/* 操作按钮 */
.action-bar { display: flex; gap: 12px; }
.act-btn { flex: 1; padding: 12px; border: none; border-radius: 8px; color: #fff; font-size: 1rem; cursor: pointer; transition: opacity 0.2s; }
.act-btn:hover { opacity: 0.9; }
.primary { background: linear-gradient(135deg,#667eea,#764ba2); }
.danger { background: #f56c6c; }
.done-tip { text-align: center; padding: 20px; color: #999; background: #fff; border-radius: 8px; }

/* 弹窗 */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 200; }
.confirm-dialog { background: #fff; border-radius: 12px; padding: 30px; width: 360px; box-shadow: 0 8px 30px rgba(0,0,0,0.15); }
.confirm-dialog h3 { margin: 0 0 20px; }
.dialog-actions { display: flex; gap: 12px; }
.cancel-btn { flex:1; padding: 10px; background: #eee; border: none; border-radius: 6px; cursor: pointer; }
.confirm-btn { flex:2; padding: 10px; background: linear-gradient(135deg,#667eea,#764ba2); color: #fff; border: none; border-radius: 6px; cursor: pointer; }
.confirm-btn:disabled { opacity: 0.6; }
.success-msg { color: #67c23a; text-align: center; margin-top: 10px; }
.error-msg { color: #f56c6c; text-align: center; margin-top: 10px; }
</style>

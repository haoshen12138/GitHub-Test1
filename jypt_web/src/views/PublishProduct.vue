<!-- jypt_web/src/views/PublishProduct.vue -->
<!-- ========== 发布商品页 ========== -->
<template>
  <div class="publish-page">
    <header class="top-nav">
      <button class="back-btn" @click="$router.push('/main')">← 返回首页</button>
      <h2>发布商品</h2>
      <div></div>
    </header>

    <div class="content-area">
      <div class="form-panel">
        <div class="form-group">
          <label>商品标题 *</label>
          <input v-model="form.title" type="text" placeholder="请输入商品标题" maxlength="100" />
        </div>

        <div class="form-group">
          <label>商品描述 *</label>
          <textarea v-model="form.description" rows="4" placeholder="请描述商品的成色、使用情况等详细信息"></textarea>
        </div>

        <div class="form-group">
          <label>价格（元）*</label>
          <input v-model="form.price" type="number" step="0.01" min="0.01" placeholder="请输入价格" />
        </div>

        <div class="form-group">
          <label>商品分类</label>
          <input
              v-model="form.category"
              type="text"
              placeholder="输入分类名，如：教材、数码、生活用品"
              list="category-list"
          />
          <datalist id="category-list">
            <option v-for="cat in existingCategories" :key="cat" :value="cat" />
          </datalist>
        </div>

        <div class="form-group">
          <label>商品图片URL（多个用逗号分隔）</label>
          <input v-model="form.images" type="text" placeholder="https://example.com/img1.jpg, https://example.com/img2.jpg" />
        </div>

        <button class="submit-btn" :disabled="submitting" @click="handlePublish">
          {{ submitting ? '发布中...' : '确认发布' }}
        </button>

        <p v-if="msg" :class="ok ? 'success-msg' : 'error-msg'">{{ msg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { publishProduct, getAllProducts } from '@/api/product'

const router = useRouter()

const currentUser = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} }
  catch { return {} }
})

const form = reactive({
  title: '',
  description: '',
  price: '',
  category: '',
  images: ''
})

const submitting = ref(false)
const msg = ref('')
const ok = ref(false)
const existingCategories = ref([])

onMounted(async () => {
  try {
    const res = await getAllProducts()
    const products = res.data || []
    const cats = [...new Set(products.map(p => p.category).filter(Boolean))]
    existingCategories.value = cats
  } catch { /* 忽略，不影响表单使用 */ }
})

async function handlePublish() {
  msg.value = ''

  if (!form.title.trim()) {
    msg.value = '请输入商品标题'
    return
  }
  if (!form.description.trim()) {
    msg.value = '请输入商品描述'
    return
  }
  if (!form.price || parseFloat(form.price) <= 0) {
    msg.value = '请输入有效的价格'
    return
  }

  submitting.value = true
  try {
    await publishProduct({
      title: form.title.trim(),
      description: form.description.trim(),
      price: parseFloat(form.price),
      category: form.category.trim() || '其他',
      images: form.images.trim() || null,
      userId: currentUser.value.id
    })
    ok.value = true
    msg.value = '发布成功！即将返回首页...'
    setTimeout(() => {
      router.push('/main')
    }, 1500)
  } catch (err) {
    ok.value = false
    msg.value = '发布失败：' + (err.message || '请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page { min-height: 100vh; background: #f5f7fa; }

.top-nav {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 30px; background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.top-nav h2 { margin: 0; font-size: 1.1rem; color: #667eea; }
.back-btn { padding: 6px 14px; background: #eee; border: none; border-radius: 4px; cursor: pointer; font-size: 0.9rem; }

.content-area { max-width: 560px; margin: 30px auto; padding: 0 20px; }

.form-panel {
  background: #fff; border-radius: 8px; padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 0.9rem; color: #555; }
.form-group input, .form-group textarea {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px;
  font-size: 0.95rem; box-sizing: border-box; font-family: inherit;
  transition: border-color 0.3s;
}
.form-group textarea { resize: vertical; }
.form-group input:focus, .form-group textarea:focus { outline: none; border-color: #667eea; }

.submit-btn {
  width: 100%; padding: 12px; background: linear-gradient(135deg,#667eea,#764ba2);
  color: #fff; border: none; border-radius: 6px; font-size: 1rem;
  cursor: pointer; transition: opacity 0.3s; margin-top: 10px;
}
.submit-btn:hover { opacity: 0.9; }
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.success-msg { color: #67c23a; text-align: center; margin-top: 14px; font-size: 0.9rem; }
.error-msg { color: #f56c6c; text-align: center; margin-top: 14px; font-size: 0.9rem; }
</style>

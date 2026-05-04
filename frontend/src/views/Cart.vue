<template>
  <div>
    <Navbar />
    <div class="page-container cart-page">
      <h1>我的购物车</h1>

      <div v-if="loading" style="text-align:center;padding:80px 0;">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p style="margin-top:16px;color:#999;">加载中...</p>
      </div>

      <div v-else-if="cartItems.length === 0" class="cart-empty">
        <div class="empty-icon">🛒</div>
        <p style="font-size:18px;margin-bottom:8px;">购物车是空的</p>
        <p style="margin-bottom:24px;">快去挑选喜欢的游戏吧！</p>
        <el-button type="danger" @click="router.push('/')">去商城逛逛</el-button>
      </div>

      <template v-else>
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <img :src="item.productImage" :alt="item.productName" @error="handleImgError" />
          <div class="cart-item-info">
            <h3>{{ item.productName }}</h3>
            <div class="cart-price">¥{{ item.productPrice }}</div>
          </div>
          <el-input-number v-model="item.quantity" :min="1" :max="99" size="small" @change="(val) => handleQuantityChange(item, val)" />
          <div style="font-size:18px;font-weight:700;color:#e94560;min-width:80px;text-align:right;">
            ¥{{ (item.productPrice * item.quantity).toFixed(2) }}
          </div>
          <el-button text type="danger" @click="handleRemove(item)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>

        <div style="display:flex;justify-content:space-between;align-items:center;margin-top:24px;padding:20px;background:#fff;border-radius:12px;">
          <div>
            <span style="color:#999;">共 {{ cartItems.length }} 件商品</span>
            <el-button text type="danger" style="margin-left:16px;" @click="handleClear">清空购物车</el-button>
          </div>
          <div style="display:flex;align-items:center;gap:20px;">
            <div>
              <span style="color:#999;">合计：</span>
              <span style="font-size:28px;font-weight:700;color:#e94560;">¥{{ total.toFixed(2) }}</span>
            </div>
            <el-button type="danger" size="large" @click="handleCheckout">去结算</el-button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import { getCart, updateCart, removeCart, clearCart } from '../api/index'

const router = useRouter()
const cartItems = ref([])
const loading = ref(true)

const total = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
})

onMounted(async () => {
  await loadCart()
})

async function loadCart() {
  loading.value = true
  try {
    const res = await getCart()
    if (res.code === 200) {
      cartItems.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载购物车失败')
  } finally {
    loading.value = false
  }
}

async function handleQuantityChange(item, val) {
  try {
    await updateCart(item.id, val)
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

async function handleRemove(item) {
  try {
    await removeCart(item.id)
    cartItems.value = cartItems.value.filter(i => i.id !== item.id)
    ElMessage.success('已移除')
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

async function handleClear() {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await clearCart()
      cartItems.value = []
      ElMessage.success('购物车已清空')
    } catch (e) {
      ElMessage.error('清空失败')
    }
  }).catch(() => {})
}

function handleCheckout() {
  ElMessage.success('结算成功！感谢您的购买 🎉')
}

function handleImgError(e) {
  e.target.src = 'https://placehold.co/460x215/1a1a2e/e94560?text=Game'
}
</script>

<template>
  <div>
    <Navbar />
    <div class="page-container">
      <div style="margin-bottom:16px;">
        <el-button text @click="router.push('/')">
          <el-icon><ArrowLeft /></el-icon> 返回商城
        </el-button>
      </div>

      <div v-if="loading" style="text-align:center;padding:80px 0;">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p style="margin-top:16px;color:#999;">加载中...</p>
      </div>

      <div v-else-if="!product" style="text-align:center;padding:80px 0;color:#999;">
        <p>商品不存在</p>
      </div>

      <div v-else class="product-detail">
        <div class="product-detail-image">
          <img :src="product.image" :alt="product.name" @error="handleImgError" />
        </div>
        <div class="product-detail-info">
          <h1>{{ product.name }}</h1>
          <div class="developer">{{ product.developer }} | {{ product.category }}</div>
          <div class="price-section">
            <span class="current" :class="{ free: product.price === 0 }">
              {{ product.price === 0 ? '免费' : '¥' + product.price }}
            </span>
            <span v-if="product.price > 0" style="color:#999;font-size:14px;">原价 ¥{{ product.price }}</span>
          </div>
          <el-tag v-if="product.stock > 999" type="success" effect="plain" style="margin-bottom:16px;">库存充足</el-tag>
          <el-tag v-else :type="product.stock > 0 ? 'warning' : 'danger'" effect="plain" style="margin-bottom:16px;">
            {{ product.stock > 0 ? '仅剩 ' + product.stock + ' 件' : '暂时缺货' }}
          </el-tag>
          <div class="description">{{ product.description }}</div>
          <div style="display:flex;gap:12px;">
            <el-input-number v-model="quantity" :min="1" :max="99" size="large" />
            <el-button type="danger" size="large" @click="addToCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import { getProduct, addToCart as apiAddToCart } from '../api/index'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(true)
const quantity = ref(1)

onMounted(async () => {
  try {
    const res = await getProduct(route.params.id)
    if (res.code === 200) {
      product.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})

async function addToCart() {
  try {
    const res = await apiAddToCart(product.value.id, quantity.value)
    if (res.code === 200) {
      ElMessage.success(`已将 ${quantity.value} 份「${product.value.name}」加入购物车`)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

function handleImgError(e) {
  e.target.src = 'https://placehold.co/460x215/1a1a2e/e94560?text=Game'
}
</script>

<template>
  <div>
    <Navbar />
    <div class="page-container">
      <div class="home-header">
        <h1>GameMall 游戏商城</h1>
        <p>发现你喜爱的游戏世界</p>
      </div>

      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索游戏..." size="large" clearable @clear="search" @keyup.enter="search">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="search"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
      </div>

      <div style="display:flex;gap:12px;margin-bottom:24px;flex-wrap:wrap;">
        <el-button :type="activeCategory === '' ? 'danger' : ''" plain @click="filterCategory('')">全部</el-button>
        <el-button
          v-for="cat in categories"
          :key="cat"
          :type="activeCategory === cat ? 'danger' : ''"
          plain
          @click="filterCategory(cat)"
        >{{ cat }}</el-button>
      </div>

      <div v-if="loading" style="text-align:center;padding:80px 0;">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p style="margin-top:16px;color:#999;">加载中...</p>
      </div>

      <div v-else-if="filteredProducts.length === 0" style="text-align:center;padding:80px 0;color:#999;">
        <el-icon :size="48"><Search /></el-icon>
        <p style="margin-top:16px;font-size:16px;">没有找到相关游戏</p>
      </div>

      <div v-else class="product-grid">
        <el-card v-for="product in filteredProducts" :key="product.id" shadow="hover" class="product-card" @click="goDetail(product.id)">
          <img :src="product.image" :alt="product.name" class="product-card-img" @error="handleImgError" />
          <div class="product-card-info">
            <h3>{{ product.name }}</h3>
            <div class="developer">{{ product.developer }}</div>
            <span class="category-tag">{{ product.category }}</span>
            <div class="product-card-price">
              <span>
                <span class="price" :class="{ free: product.price === 0 }">
                  {{ product.price === 0 ? '免费' : '¥' + product.price }}
                </span>
              </span>
              <el-button type="danger" size="small" plain @click.stop="addToCart(product)">
                <el-icon><ShoppingCart /></el-icon> 加入购物车
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import { getProducts, addToCart as apiAddToCart } from '../api/index'

const router = useRouter()
const products = ref([])
const keyword = ref('')
const loading = ref(true)
const activeCategory = ref('')

const categories = computed(() => {
  const cats = new Set(products.value.map(p => p.category))
  return [...cats]
})

const filteredProducts = computed(() => {
  let list = products.value
  if (activeCategory.value) {
    list = list.filter(p => p.category === activeCategory.value)
  }
  if (keyword.value) {
    const kw = keyword.value.toLowerCase()
    list = list.filter(p => p.name.toLowerCase().includes(kw) || p.description?.toLowerCase().includes(kw))
  }
  return list
})

onMounted(async () => {
  await loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProducts()
    if (res.code === 200) {
      products.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

function search() {
  // Client-side search is already handled by filteredProducts computed
}

function filterCategory(cat) {
  activeCategory.value = activeCategory.value === cat ? '' : cat
}

function goDetail(id) {
  router.push(`/product/${id}`)
}

async function addToCart(product) {
  try {
    const res = await apiAddToCart(product.id, 1)
    if (res.code === 200) {
      ElMessage.success(`已将「${product.name}」加入购物车`)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

function handleImgError(e) {
  e.target.src = 'https://placehold.co/460x215/1a1a2e/e94560?text=GameMall'
}
</script>

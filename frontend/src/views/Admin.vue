<template>
  <div>
    <Navbar />
    <div class="admin-container">
      <div class="admin-sidebar">
        <el-menu :default-active="activeTab" @select="activeTab = $event">
          <el-menu-item index="dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="add">
            <el-icon><Plus /></el-icon>
            <span>添加商品</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="admin-main">
        <!-- Dashboard -->
        <div v-if="activeTab === 'dashboard'">
          <h2>数据概览</h2>
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-number">{{ users.length }}</div>
              <div class="stat-label">注册用户</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ products.length }}</div>
              <div class="stat-label">商品总数</div>
            </div>
            <div class="stat-card">
              <div class="stat-number" style="color:#67c23a;">¥{{ totalValue }}</div>
              <div class="stat-label">商品总价值</div>
            </div>
          </div>
          <el-card>
            <template #header><span>最近添加的商品</span></template>
            <el-table :data="latestProducts" style="width:100%" stripe>
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="category" label="分类" width="120" />
              <el-table-column prop="price" label="价格" width="100">
                <template #default="{row}">{{ row.price === 0 ? '免费' : '¥' + row.price }}</template>
              </el-table-column>
              <el-table-column prop="createdAt" label="添加时间" width="180" />
            </el-table>
          </el-card>
        </div>

        <!-- Users -->
        <div v-if="activeTab === 'users'">
          <h2>用户管理</h2>
          <el-card>
            <el-table :data="users" style="width:100%" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="{row}">
                  <el-tag :type="row.role === 'admin' ? 'danger' : ''" size="small">
                    {{ row.role === 'admin' ? '管理员' : '用户' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="注册时间" width="180" />
            </el-table>
          </el-card>
        </div>

        <!-- Products -->
        <div v-if="activeTab === 'products'">
          <h2>商品管理</h2>
          <el-card>
            <el-table :data="products" style="width:100%" stripe>
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column label="图片" width="100">
                <template #default="{row}">
                  <img :src="row.image" style="width:80px;height:45px;object-fit:cover;border-radius:4px;" @error="handleImgError" />
                </template>
              </el-table-column>
              <el-table-column prop="name" label="名称" min-width="150" />
              <el-table-column prop="category" label="分类" width="100" />
              <el-table-column prop="price" label="价格" width="80">
                <template #default="{row}">{{ row.price === 0 ? '免费' : '¥' + row.price }}</template>
              </el-table-column>
              <el-table-column prop="stock" label="库存" width="60" />
              <el-table-column label="操作" width="150">
                <template #default="{row}">
                  <el-button type="primary" size="small" @click="editProduct(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- Add Product -->
        <div v-if="activeTab === 'add' || activeTab === 'edit'">
          <h2>{{ isEditing ? '编辑商品' : '添加新商品' }}</h2>
          <el-card style="max-width:700px;">
            <el-form :model="productForm" label-width="100px">
              <el-form-item label="商品名称" required>
                <el-input v-model="productForm.name" placeholder="请输入游戏名称" />
              </el-form-item>
              <el-form-item label="分类" required>
                <el-select v-model="productForm.category" placeholder="请选择分类" style="width:100%">
                  <el-option label="FPS" value="FPS" />
                  <el-option label="MOBA" value="MOBA" />
                  <el-option label="开放世界" value="开放世界" />
                  <el-option label="动作RPG" value="动作RPG" />
                  <el-option label="大逃杀" value="大逃杀" />
                  <el-option label="生存" value="生存" />
                  <el-option label="策略" value="策略" />
                  <el-option label="模拟经营" value="模拟经营" />
                  <el-option label="体育" value="体育" />
                  <el-option label="战术FPS" value="战术FPS" />
                  <el-option label="CRPG" value="CRPG" />
                  <el-option label="开放世界RPG" value="开放世界RPG" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
              <el-form-item label="开发商">
                <el-input v-model="productForm.developer" placeholder="游戏开发商" />
              </el-form-item>
              <el-form-item label="价格" required>
                <el-input-number v-model="productForm.price" :min="0" :precision="2" :step="10" style="width:200px" />
                <span style="margin-left:8px;color:#999;">元（免费填0）</span>
              </el-form-item>
              <el-form-item label="库存">
                <el-input-number v-model="productForm.stock" :min="0" :max="99999" />
              </el-form-item>
              <el-form-item label="商品图片">
                <div style="display:flex;gap:12px;align-items:center;flex-wrap:wrap;">
                  <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/*">
                    <el-button type="primary"><el-icon><Upload /></el-icon> 上传图片</el-button>
                  </el-upload>
                  <el-input v-model="productForm.image" placeholder="或输入图片URL" style="flex:1;min-width:200px;" />
                </div>
                <img v-if="productForm.image" :src="productForm.image" style="margin-top:8px;width:200px;height:112px;object-fit:cover;border-radius:8px;" @error="handleImgError" />
              </el-form-item>
              <el-form-item label="商品描述">
                <el-input v-model="productForm.description" type="textarea" :rows="4" placeholder="游戏的详细介绍..." />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" :loading="saving" @click="handleSave">
                  {{ isEditing ? '保存修改' : '添加商品' }}
                </el-button>
                <el-button v-if="isEditing" @click="cancelEdit">取消</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import { getUsers, getAdminProducts, addProduct, updateProduct, deleteProduct, uploadImage } from '../api/index'

const activeTab = ref('dashboard')
const users = ref([])
const products = ref([])
const saving = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

const productForm = ref({
  name: '',
  category: '',
  developer: '',
  price: 0,
  stock: 999,
  image: '',
  description: ''
})

const totalValue = computed(() => {
  return products.value.reduce((sum, p) => sum + p.price, 0).toFixed(2)
})

const latestProducts = computed(() => {
  return [...products.value].slice(-5).reverse()
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    const [userRes, prodRes] = await Promise.all([getUsers(), getAdminProducts()])
    if (userRes.code === 200) users.value = userRes.data
    if (prodRes.code === 200) products.value = prodRes.data
  } catch (e) {
    ElMessage.error('加载数据失败')
  }
}

function editProduct(product) {
  isEditing.value = true
  editingId.value = product.id
  productForm.value = {
    name: product.name,
    category: product.category,
    developer: product.developer || '',
    price: product.price,
    stock: product.stock,
    image: product.image || '',
    description: product.description || ''
  }
  activeTab.value = 'edit'
}

function cancelEdit() {
  isEditing.value = false
  editingId.value = null
  resetForm()
  activeTab.value = 'products'
}

function resetForm() {
  productForm.value = { name: '', category: '', developer: '', price: 0, stock: 999, image: '', description: '' }
}

async function handleSave() {
  const form = productForm.value
  if (!form.name) { ElMessage.warning('请输入商品名称'); return }
  if (!form.category) { ElMessage.warning('请选择分类'); return }

  saving.value = true
  try {
    let res
    if (isEditing.value) {
      res = await updateProduct(editingId.value, form)
    } else {
      res = await addProduct(form)
    }
    if (res.code === 200) {
      ElMessage.success(isEditing.value ? '修改成功' : '添加成功')
      await loadData()
      cancelEdit()
      activeTab.value = 'products'
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(product) {
  ElMessageBox.confirm(`确定要删除「${product.name}」吗？`, '提示', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteProduct(product.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        await loadData()
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

async function handleUpload(options) {
  const file = options.file
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片不能超过10MB')
    return
  }
  try {
    const res = await uploadImage(file)
    if (res.code === 200) {
      productForm.value.image = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

function handleImgError(e) {
  e.target.src = 'https://placehold.co/460x215/1a1a2e/e94560?text=Game'
}
</script>

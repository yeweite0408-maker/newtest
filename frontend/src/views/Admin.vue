<template>
  <div>
    <Navbar />
    <div class="admin-container">
      <div class="admin-sidebar">
        <el-menu :default-active="tab" @select="tab = $event">
          <el-menu-item index="articles"><el-icon><Document /></el-icon> 文章管理</el-menu-item>
          <el-menu-item index="editor" v-if="showEditor"><el-icon><Edit /></el-icon> 编辑文章</el-menu-item>
          <el-menu-item index="users"><el-icon><User /></el-icon> 用户管理</el-menu-item>
        </el-menu>
      </div>
      <div class="admin-main">
        <div v-if="tab === 'articles'" class="admin-section">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
            <h2 style="margin:0;font-size:20px;font-weight:600">文章管理</h2>
            <el-button type="primary" @click="openEditor()">写文章</el-button>
          </div>
          <el-table :data="articles" stripe style="width:100%" size="small">
            <el-table-column prop="id" label="ID" width="50" />
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="category" label="分类" width="80" />
            <el-table-column prop="authorName" label="作者" width="80" />
            <el-table-column prop="status" label="状态" width="60" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" @click="openEditor(row)">编辑</el-button>
                <el-button size="small" type="danger" plain @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="tab === 'editor'" class="admin-section" style="max-width:800px">
          <h2 style="font-size:20px;font-weight:600;margin-bottom:20px">{{ editing?.id ? '编辑文章' : '写文章' }}</h2>
          <el-form label-width="60px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="文章标题" size="large" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="form.category" style="width:100%">
                <el-option label="技术分享" value="技术分享" />
                <el-option label="前端开发" value="前端开发" />
                <el-option label="项目实战" value="项目实战" />
                <el-option label="开发工具" value="开发工具" />
              </el-select>
            </el-form-item>
            <el-form-item label="标签">
              <el-input v-model="form.tags" placeholder="用逗号分隔" />
            </el-form-item>
            <el-form-item label="摘要">
              <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" />
            </el-form-item>
            <el-form-item label="配图">
              <div style="display:flex;gap:8px;flex-wrap:wrap">
                <el-upload class="upload-btn" :auto-upload="false" :show-file-list="false" accept="image/*" @change="handleUpload">
                  <el-button size="small">选择图片</el-button>
                </el-upload>
                <a v-if="form.coverImage" :href="form.coverImage" target="_blank" style="font-size:12px;color:#0071e3;line-height:32px">查看图片</a>
              </div>
            </el-form-item>
            <el-form-item label="内容">
              <div style="width:100%">
                <div style="display:flex;gap:6px;margin-bottom:8px;flex-wrap:wrap">
                  <el-tooltip content="插入图片" placement="top">
                    <el-button size="small" @click="$refs.imgInput.click()">
                      <el-icon><PictureFilled /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <input ref="imgInput" type="file" accept="image/*" style="display:none" @change="insertImage" />
                  <span style="font-size:12px;color:#86868b;line-height:28px">支持拖拽/粘贴图片到编辑器</span>
                </div>
                <el-input v-model="form.content" type="textarea" :rows="18" placeholder="开始写文章..." @paste="handlePaste" />
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="handleSave">{{ editing?.id ? '更新' : '发布' }}</el-button>
              <el-button size="large" @click="tab = 'articles'">取消</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="tab === 'users'" class="admin-section">
          <h2 style="font-size:20px;font-weight:600;margin-bottom:20px">用户管理</h2>
          <el-table :data="users" stripe style="width:100%" size="small">
            <el-table-column prop="id" label="ID" width="50" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" width="60" />
            <el-table-column prop="createdAt" label="注册时间" width="160" />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { getAdminArticles, createAdminArticle, updateAdminArticle, deleteAdminArticle, getUsers, uploadImage } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const tab = ref('articles')
const showEditor = ref(false)
const articles = ref([])
const users = ref([])
const editing = ref(null)
const imgInput = ref(null)
const form = ref({ title: '', category: '技术分享', tags: '', summary: '', coverImage: '', content: '' })

async function fetchData() {
  try { const r1 = await getAdminArticles(); articles.value = r1.data || [] } catch {}
  try { const r2 = await getUsers(); users.value = r2.data || [] } catch {}
}

function openEditor(article) {
  if (article) {
    editing.value = article
    form.value = { title: article.title, category: article.category, tags: article.tags || '', summary: article.summary || '', coverImage: article.coverImage || '', content: article.content || '' }
  } else {
    editing.value = null
    form.value = { title: '', category: '技术分享', tags: '', summary: '', coverImage: '', content: '' }
  }
  showEditor.value = true
  tab.value = 'editor'
}

async function handleUpload(file) {
  try {
    const res = await uploadImage(file.raw)
    form.value.coverImage = res.data.url
    ElMessage.success('图片已上传')
  } catch { ElMessage.error('上传失败') }
}

async function insertImage(e) {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadImage(file)
    form.value.content += `\n![image](${res.data.url})\n`
    ElMessage.success('图片已插入')
  } catch { ElMessage.error('上传失败') }
  e.target.value = ''
}

async function handlePaste(e) {
  const items = e.clipboardData?.items
  if (!items) return
  for (const item of items) {
    if (item.type.startsWith('image/')) {
      e.preventDefault()
      const file = item.getAsFile()
      if (!file) continue
      try {
        const res = await uploadImage(file)
        form.value.content += `\n![image](${res.data.url})\n`
        ElMessage.success('图片已插入')
      } catch { ElMessage.error('上传失败') }
      return
    }
  }
}

async function handleSave() {
  if (!form.value.title.trim()) { ElMessage.warning('请输入标题'); return }
  try {
    if (editing.value?.id) {
      await updateAdminArticle(editing.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createAdminArticle(form.value)
      ElMessage.success('发布成功')
    }
    tab.value = 'articles'
    showEditor.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }).then(async () => {
    await deleteAdminArticle(id); ElMessage.success('已删除'); fetchData()
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
  if (route.query.write === '1') openEditor()
})
</script>

<style scoped>
.upload-btn { display: inline-flex; }
</style>

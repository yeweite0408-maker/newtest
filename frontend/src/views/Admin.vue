<template>
  <div>
    <Navbar />
    <div class="admin-container">
      <div class="admin-sidebar">
        <el-menu :default-active="tab" @select="tab = $event">
          <el-menu-item index="articles"><el-icon><Document /></el-icon> 文章管理</el-menu-item>
          <el-menu-item index="editor" v-if="editing"><el-icon><Edit /></el-icon> 编辑文章</el-menu-item>
          <el-menu-item index="users"><el-icon><User /></el-icon> 用户管理</el-menu-item>
        </el-menu>
      </div>
      <div class="admin-main">
        <div v-if="tab === 'articles'" class="admin-section">
          <div style="display:flex;justify-content:space-between;margin-bottom:16px;">
            <h2 style="margin:0">文章管理</h2>
            <el-button type="primary" @click="openEditor()">写文章</el-button>
          </div>
          <el-table :data="articles" stripe style="width:100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="标题" min-width="200" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="authorName" label="作者" width="100" />
            <el-table-column prop="status" label="状态" width="80" />
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button size="small" @click="openEditor(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="tab === 'editor'" class="admin-section">
          <h2>{{ editing?.id ? '编辑文章' : '写文章' }}</h2>
          <el-form label-width="80px">
            <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
            <el-form-item label="分类">
              <el-select v-model="form.category">
                <el-option label="技术分享" value="技术分享" />
                <el-option label="前端开发" value="前端开发" />
                <el-option label="项目实战" value="项目实战" />
                <el-option label="开发工具" value="开发工具" />
              </el-select>
            </el-form-item>
            <el-form-item label="标签"><el-input v-model="form.tags" placeholder="用逗号分隔" /></el-form-item>
            <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
            <el-form-item label="内容">
              <el-input v-model="form.content" type="textarea" :rows="15" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">{{ editing?.id ? '更新' : '发布' }}</el-button>
              <el-button @click="tab = 'articles'">取消</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="tab === 'users'" class="admin-section">
          <h2>用户管理</h2>
          <el-table :data="users" stripe style="width:100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" width="80" />
            <el-table-column prop="createdAt" label="注册时间" />
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { getAdminArticles, createAdminArticle, updateAdminArticle, deleteAdminArticle, getUsers } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const tab = ref('articles')
const articles = ref([])
const users = ref([])
const editing = ref(null)
const form = ref({ title: '', category: '技术分享', tags: '', summary: '', content: '' })

async function fetchData() {
  try { const r1 = await getAdminArticles(); articles.value = r1.data || [] } catch {}
  try { const r2 = await getUsers(); users.value = r2.data || [] } catch {}
}

function openEditor(article) {
  if (article) {
    editing.value = article
    form.value = { title: article.title, category: article.category, tags: article.tags || '', summary: article.summary || '', content: article.content || '' }
  } else {
    editing.value = null
    form.value = { title: '', category: '技术分享', tags: '', summary: '', content: '' }
  }
  tab.value = 'editor'
}

async function handleSave() {
  try {
    if (editing.value?.id) {
      await updateAdminArticle(editing.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createAdminArticle(form.value)
      ElMessage.success('发布成功')
    }
    tab.value = 'articles'
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => {
    await deleteAdminArticle(id); ElMessage.success('已删除'); fetchData()
  }).catch(() => {})
}

onMounted(fetchData)
</script>

<style scoped>
.admin-container { display: flex; min-height: calc(100vh - 60px); }
.admin-sidebar { width: 200px; background: #fff; border-right: 1px solid #e0e0e0; }
.admin-main { flex: 1; padding: 24px; }
.admin-section { max-width: 900px; }
</style>

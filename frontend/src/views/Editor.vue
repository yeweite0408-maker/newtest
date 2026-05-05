<template>
  <div style="min-height:100vh;background:var(--bg-color)">
    <div style="max-width:800px;margin:0 auto;padding:20px">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
        <h1 style="font-size:20px;font-weight:700;margin:0">{{ articleId ? '编辑文章' : '写文章' }}</h1>
        <div>
          <el-button @click="$router.push('/')">返回</el-button>
          <el-button type="primary" @click="saveArticle('published')">发布</el-button>
          <el-button @click="saveArticle('draft')">存草稿</el-button>
        </div>
      </div>
      <el-input v-model="title" placeholder="文章标题..." size="large" style="font-size:24px;font-weight:700;margin-bottom:16px" />
      <div style="display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap">
        <el-input v-model="summary" placeholder="文章摘要（可选）" style="flex:1;min-width:200px" />
        <el-select v-model="category" placeholder="分类" style="width:140px">
          <el-option label="技术分享" value="技术分享" />
          <el-option label="前端开发" value="前端开发" />
          <el-option label="项目实战" value="项目实战" />
          <el-option label="开发工具" value="开发工具" />
          <el-option v-for="c in categories" :key="c.name" :label="c.name" :value="c.name" />
        </el-select>
        <el-input v-model="tags" placeholder="标签，逗号分隔" style="width:200px" />
      </div>
      <div style="margin-bottom:8px;display:flex;gap:6px;flex-wrap:wrap;align-items:center">
        <el-upload :auto-upload="false" :show-file-list="false" accept="image/*" @change="insertImage">
          <el-button size="small"><el-icon><PictureFilled /></el-icon> 图片</el-button>
        </el-upload>
        <el-tooltip content="支持粘贴图片" placement="top">
          <span style="font-size:12px;color:var(--text-secondary)">📋 支持粘贴图片</span>
        </el-tooltip>
      </div>
      <el-input v-model="content" type="textarea" :rows="22" placeholder="开始写作... 支持 Markdown 格式" @paste="handlePaste" style="font-size:15px;line-height:1.8" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticle, createArticle, updateArticle, uploadImage, getCategories } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const articleId = ref(route.params.id)
const title = ref('')
const summary = ref('')
const category = ref('技术分享')
const tags = ref('')
const content = ref('')
const categories = ref([])

onMounted(async () => {
  try { const r = await getCategories(); categories.value = r.data || [] } catch {}
  if (articleId.value) {
    try { const r = await getArticle(articleId.value); const a = r.data; title.value = a.title; summary.value = a.summary || ''; category.value = a.category; tags.value = a.tags || ''; content.value = a.content || '' } catch { ElMessage.error('文章不存在'); router.push('/') }
  }
})

async function saveArticle(status) {
  if (!title.value.trim()) { ElMessage.warning('请输入标题'); return }
  const data = { title: title.value, content: content.value, summary: summary.value, category: category.value, tags: tags.value, status }
  try {
    if (articleId.value) { await updateArticle(articleId.value, data); ElMessage.success('已保存') } else { await createArticle(data); ElMessage.success('发布成功') }
    router.push('/')
  } catch { ElMessage.error('保存失败') }
}

async function insertImage(file) {
  try { const res = await uploadImage(file.raw); content.value += `\n![image](${res.data.url})\n`; ElMessage.success('图片已插入') } catch { ElMessage.error('上传失败') }
}

async function handlePaste(e) {
  const items = e.clipboardData?.items
  if (!items) return
  for (const item of items) {
    if (item.type.startsWith('image/')) { e.preventDefault(); const file = item.getAsFile(); if (!file) continue; try { const res = await uploadImage(file); content.value += `\n![image](${res.data.url})\n`; ElMessage.success('图片已插入') } catch { ElMessage.error('上传失败') }; return }
  }
}
</script>

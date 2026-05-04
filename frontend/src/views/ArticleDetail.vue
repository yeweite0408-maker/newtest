<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width: 800px; margin: 0 auto;">
      <div style="margin-bottom:16px;">
        <el-button @click="$router.push('/')">← 返回首页</el-button>
      </div>
      <div v-if="article" class="article-detail">
        <h1>{{ article.title }}</h1>
        <div class="meta">
          <span><el-icon><User /></el-icon> {{ article.authorName }}</span>
          <span><el-icon><Calendar /></el-icon> {{ article.createdAt?.substring(0, 10) }}</span>
          <el-tag size="small">{{ article.category }}</el-tag>
        </div>
        <div class="content" v-html="renderContent"></div>

        <el-divider />
        <h3>评论 ({{ comments.length }})</h3>
        <div class="comment-form" v-if="userStore.isLoggedIn">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评论..." />
          <el-button type="primary" style="margin-top:8px" @click="submitComment">发表评论</el-button>
        </div>
        <div v-else style="color:#999;margin:12px 0">
          请<router-link to="/login">登录</router-link>后发表评论
        </div>

        <div v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :size="28">{{ c.username?.[0] }}</el-avatar>
            <span class="comment-user">{{ c.username }}</span>
            <span class="comment-date">{{ c.createdAt?.substring(0, 16) }}</span>
          </div>
          <p class="comment-content">{{ c.content }}</p>
        </div>
        <el-empty v-if="comments.length === 0" description="暂无评论" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getArticle, getComments, addComment } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const article = ref(null)
const comments = ref([])
const newComment = ref('')

const renderContent = computed(() => {
  if (!article.value?.content) return ''
  return article.value.content.replace(/\n/g, '<br>')
})

async function fetchData() {
  try {
    const [a, c] = await Promise.all([getArticle(route.params.id), getComments(route.params.id)])
    article.value = a.data
    comments.value = c.data || []
  } catch {}
}

async function submitComment() {
  if (!newComment.value.trim()) return
  try {
    await addComment({ articleId: article.value.id, content: newComment.value })
    ElMessage.success('评论成功')
    newComment.value = ''
    const res = await getComments(route.params.id)
    comments.value = res.data || []
  } catch {}
}

onMounted(fetchData)
</script>

<style scoped>
.article-detail h1 { font-size: 26px; margin-bottom: 12px; }
.meta { display: flex; gap: 16px; color: #999; font-size: 14px; align-items: center; margin-bottom: 24px; }
.content { font-size: 15px; line-height: 1.8; color: #333; }
.comment-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.comment-header { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.comment-user { font-weight: 500; font-size: 14px; }
.comment-date { color: #999; font-size: 12px; }
.comment-content { margin: 0; color: #333; font-size: 14px; }
</style>

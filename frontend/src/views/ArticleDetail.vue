<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width:720px;margin:0 auto;">
      <div class="back-btn" @click="$router.push('/')">← 返回首页</div>

      <div v-if="article" class="article-detail">
        <div style="display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap">
          <el-tag size="small">{{ article.category }}</el-tag>
          <span style="color:#86868b;font-size:12px;line-height:22px">{{ article.createdAt?.substring(0, 10) }}</span>
        </div>
        <h1>{{ article.title }}</h1>

        <div style="display:flex;align-items:center;gap:8px;margin-bottom:28px;padding:12px 0;border-bottom:1px solid #f0f0f0">
          <el-avatar :size="28" style="background:#f0f0f0;color:#515154;font-weight:600;font-size:12px">{{ article.authorName?.[0] }}</el-avatar>
          <span style="font-size:13px;color:#515154;font-weight:500">{{ article.authorName }}</span>
        </div>

        <img v-if="article.coverImage" :src="article.coverImage" style="width:100%;border-radius:12px;margin-bottom:24px" />

        <div class="content" v-html="renderContent"></div>

        <hr class="apple-divider" />

        <h3 style="font-size:17px;font-weight:600;margin-bottom:16px">评论 ({{ comments.length }})</h3>

        <div v-if="userStore.isLoggedIn" class="comment-form" style="margin-bottom:20px">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评论..." style="margin-bottom:8px" />
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">发表评论</el-button>
        </div>
        <div v-else style="color:#86868b;margin:12px 0;font-size:14px">
          请<router-link to="/login" style="color:#0071e3">登录</router-link>后发表评论
        </div>

        <div v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :size="26" style="background:#f0f0f0;color:#515154;font-weight:600;font-size:11px">{{ c.username?.[0] }}</el-avatar>
            <span class="comment-user">{{ c.username }}</span>
            <span class="comment-date">{{ c.createdAt?.substring(0, 16) }}</span>
          </div>
          <p class="comment-content">{{ c.content }}</p>
        </div>
        <el-empty v-if="comments.length === 0" description="暂无评论" :image-size="60" style="padding:20px 0" />
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

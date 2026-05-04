<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width:720px;margin:0 auto">
      <div v-if="profile" class="profile-header" style="text-align:center;padding:32px 0">
        <el-avatar :size="72" style="background:#f0f0f0;color:#515154;font-weight:700;font-size:28px;margin:0 auto 12px">{{ profile.user?.username?.[0]?.toUpperCase() }}</el-avatar>
        <h1 style="font-size:24px;font-weight:700;margin-bottom:4px">{{ profile.user?.username }}</h1>
        <p style="color:var(--text-secondary);font-size:14px">{{ profile.user?.role === 'admin' ? '管理员' : '用户' }} · {{ profile.articles?.length || 0 }} 篇文章</p>
        <div v-if="isMe" style="margin-top:12px">
          <router-link to="/settings"><el-button size="small">修改密码</el-button></router-link>
        </div>
      </div>

      <div style="display:flex;gap:12px;margin-bottom:16px">
        <el-radio-group v-model="tab">
          <el-radio-button value="articles">文章</el-radio-button>
          <el-radio-button value="likes">收藏</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="tab === 'articles'" class="article-list">
        <div v-for="article in profile.articles" :key="article.id" class="article-card" @click="$router.push('/article/' + article.id)">
          <div class="card-header">
            <el-tag size="small">{{ article.category }}</el-tag>
            <span class="date">{{ article.createdAt?.substring(0, 10) }}</span>
          </div>
          <h2 class="article-title">{{ article.title }}</h2>
          <p class="summary">{{ article.summary }}</p>
          <div style="color:var(--text-secondary);font-size:12px">👁 {{ article.viewsCount || 0 }} · ❤ {{ article.likesCount || 0 }}</div>
        </div>
        <el-empty v-if="!profile.articles?.length" description="暂无文章" :image-size="60" style="padding:30px 0" />
      </div>

      <div v-if="tab === 'likes'" class="article-list">
        <div v-for="article in likedArticles" :key="article.id" class="article-card" @click="$router.push('/article/' + article.id)">
          <div class="card-header">
            <el-tag size="small">{{ article.category }}</el-tag>
            <span class="date">{{ article.createdAt?.substring(0, 10) }}</span>
          </div>
          <h2 class="article-title">{{ article.title }}</h2>
          <p class="summary">{{ article.summary }}</p>
          <div style="color:var(--text-secondary);font-size:12px">❤ {{ article.likesCount || 0 }}</div>
        </div>
        <el-empty v-if="!likedArticles.length" description="暂无收藏" :image-size="60" style="padding:30px 0" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { getUserProfile, getLikedArticles } from '../api'

const route = useRoute()
const profile = ref(null)
const likedArticles = ref([])
const tab = ref('articles')
const user = JSON.parse(localStorage.getItem('user') || 'null')
const isMe = ref(Number(route.params.id) === user?.id)

onMounted(async () => {
  try {
    const [p, l] = await Promise.all([
      getUserProfile(route.params.id),
      getLikedArticles(route.params.id)
    ])
    profile.value = p.data
    likedArticles.value = l.data || []
  } catch {}
})
</script>

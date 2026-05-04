<template>
  <div>
    <Navbar />
    <div class="page-container">
      <div class="hero">
        <h1>MyTest</h1>
        <p>分享技术，记录生活</p>
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索文章..." clearable @keyup.enter="handleSearch" size="large">
            <template #prefix><el-icon style="font-size:16px"><Search /></el-icon></template>
          </el-input>
        </div>
      </div>

      <div class="categories">
        <el-radio-group v-model="currentCategory" @change="filterByCategory">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="技术分享">技术分享</el-radio-button>
          <el-radio-button value="前端开发">前端开发</el-radio-button>
          <el-radio-button value="项目实战">项目实战</el-radio-button>
          <el-radio-button value="开发工具">开发工具</el-radio-button>
        </el-radio-group>
      </div>

      <div class="article-list">
        <div v-for="article in articles" :key="article.id" class="article-card" @click="$router.push('/article/' + article.id)">
          <div class="card-header">
            <el-tag size="small">{{ article.category }}</el-tag>
            <span class="date">{{ article.createdAt?.substring(0, 10) }}</span>
          </div>
          <h2 class="article-title">{{ article.title }}</h2>
          <p class="summary">{{ article.summary }}</p>
          <div class="card-footer">
            <span class="author">
              <el-icon style="font-size:12px"><User /></el-icon>
              {{ article.authorName }}
            </span>
            <span style="color:var(--text-secondary);font-size:12px">
              👁 {{ article.viewsCount || 0 }} · ❤ {{ article.likesCount || 0 }}
            </span>
          </div>
        </div>
        <el-empty v-if="articles.length === 0" description="暂无文章" :image-size="80" />
      </div>
    </div>

    <button v-if="userStore.isLoggedIn" class="fab" title="写文章" @click="$router.push('/admin?write=1')">+</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getArticles, searchArticles } from '../api'

const router = useRouter()
const userStore = useUserStore()
const articles = ref([])
const keyword = ref('')
const currentCategory = ref('')

async function fetchArticles() {
  try { const res = await getArticles(); articles.value = res.data || [] } catch {}
}
async function handleSearch() {
  if (!keyword.value.trim()) { fetchArticles(); return }
  try { const res = await searchArticles(keyword.value); articles.value = res.data || [] } catch {}
}
async function filterByCategory(category) {
  currentCategory.value = category
  if (!category) { fetchArticles(); return }
  try {
    const res = await getArticles()
    articles.value = (res.data || []).filter(a => a.category === category)
  } catch {}
}

onMounted(fetchArticles)
</script>

<template>
  <div>
    <Navbar />
    <div class="page-container">
      <div class="hero">
        <h1>个人博客</h1>
        <p>分享技术，记录生活</p>
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索文章..." clearable @keyup.enter="handleSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
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
            <span class="author"><el-icon><User /></el-icon> {{ article.authorName }}</span>
            <span v-if="article.tags">
              <el-tag v-for="tag in article.tags.split(',')" :key="tag" size="small" class="tag">{{ tag.trim() }}</el-tag>
            </span>
          </div>
        </div>
        <el-empty v-if="articles.length === 0" description="暂无文章" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { getArticles, searchArticles } from '../api'

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

<style scoped>
.hero { text-align: center; padding: 40px 0 20px; }
.hero h1 { font-size: 28px; margin: 0 0 8px; }
.hero p { color: #666; margin: 0 0 16px; }
.search-bar { max-width: 400px; margin: 0 auto; }
.categories { text-align: center; margin: 16px 0; }
.article-list { max-width: 800px; margin: 0 auto; }
.article-card { background: #fff; border-radius: 8px; padding: 20px; margin-bottom: 16px; cursor: pointer; border: 1px solid #eee; transition: box-shadow .2s; }
.article-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,.1); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.article-title { font-size: 20px; margin: 0 0 8px; color: #1a1a1a; }
.summary { color: #666; font-size: 14px; line-height: 1.6; margin: 0 0 12px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.author { color: #999; font-size: 13px; display: flex; align-items: center; gap: 4px; }
.tag { margin-right: 4px; }
.date { color: #999; font-size: 13px; }
</style>

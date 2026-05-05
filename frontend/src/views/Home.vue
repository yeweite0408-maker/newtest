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

      <!-- 标签云 -->
      <div class="tag-cloud" v-if="tags.length > 0">
        <el-tag v-for="tag in tags" :key="tag" :class="{ active: activeTag === tag }" @click="filterByTag(tag)" size="small" class="cloud-tag">{{ tag }}</el-tag>
        <el-tag v-if="activeTag" size="small" @click="activeTag='';fetchArticles()" style="cursor:pointer;background:transparent;color:#86868b">✕ 清除</el-tag>
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
            <div style="display:flex;gap:6px;align-items:center">
              <el-tag size="small">{{ article.category }}</el-tag>
              <el-tag v-if="article.isPinned" size="small" type="warning" style="border:none">📌 置顶</el-tag>
            </div>
            <span class="date">{{ article.createdAt?.substring(0, 10) }}</span>
          </div>
          <h2 class="article-title">{{ article.title }}</h2>
          <p class="summary">{{ article.summary }}</p>
          <div class="card-footer">
            <span class="author"><el-icon style="font-size:12px"><User /></el-icon> {{ article.authorName }}</span>
            <span style="color:var(--text-secondary);font-size:12px">👁 {{ article.viewsCount || 0 }} · ❤ {{ article.likesCount || 0 }}</span>
          </div>
        </div>
        <el-empty v-if="articles.length === 0" description="暂无文章" :image-size="80" />
      </div>
    </div>
    <button v-if="userStore.isLoggedIn" class="fab" title="写文章" @click="goWrite">+</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getArticles, searchArticles, getTags } from '../api'

const router = useRouter()
const userStore = useUserStore()
const articles = ref([])
const keyword = ref('')
const currentCategory = ref('')
const tags = ref([])
const activeTag = ref('')

async function fetchArticles() {
  try { const res = await getArticles(); articles.value = res.data || [] } catch {}
}
async function fetchTags() {
  try { const res = await getTags(); tags.value = (res.data || []).filter(Boolean).flatMap(t => t.split(',')).map(t => t.trim()).filter(Boolean) } catch {}
}
async function handleSearch() {
  if (!keyword.value.trim()) { fetchArticles(); return }
  try { const res = await searchArticles(keyword.value); articles.value = res.data || [] } catch {}
}
async function filterByCategory(category) { currentCategory.value = category; activeTag.value = ''; if (!category) { fetchArticles(); return }; try { const res = await getArticles(); articles.value = (res.data || []).filter(a => a.category === category) } catch {} }
async function filterByTag(tag) { activeTag.value = tag; try { const res = await getArticles(); articles.value = (res.data || []).filter(a => a.tags?.includes(tag)) } catch {} }
function goWrite() { router.push('/editor') }

onMounted(() => { fetchArticles(); fetchTags() })
</script>

<style scoped>
.tag-cloud { text-align: center; margin-bottom: 16px; }
.cloud-tag { margin: 3px; cursor: pointer; transition: all .15s; }
.cloud-tag:hover, .cloud-tag.active { transform: scale(1.05); }
.cloud-tag.active { background: #0071e3 !important; color: #fff !important; }
</style>

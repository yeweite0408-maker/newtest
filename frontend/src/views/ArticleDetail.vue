<template>
  <div>
    <Navbar />
    <div class="reading-progress" :style="{ width: progress + '%' }"></div>
    <div class="page-container" style="max-width:720px;margin:0 auto;">
      <div class="back-btn" @click="$router.push('/')">← 返回首页</div>

      <div v-if="article" class="article-detail">
        <div style="display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap;align-items:center">
          <el-tag size="small">{{ article.category }}</el-tag>
          <span style="color:var(--text-secondary);font-size:12px">{{ article.createdAt?.substring(0, 10) }}</span>
          <span style="color:var(--text-secondary);font-size:12px">👁 {{ article.viewsCount || 0 }}</span>
          <span style="color:var(--text-secondary);font-size:12px">❤ {{ article.likesCount || 0 }}</span>
        </div>
        <h1>{{ article.title }}</h1>

        <div style="display:flex;align-items:center;gap:12px;margin-bottom:24px;padding:12px 0;border-bottom:1px solid var(--border-color)">
          <router-link :to="'/profile/' + article.authorId" style="display:flex;align-items:center;gap:8px;color:inherit;text-decoration:none">
            <el-avatar :size="28" style="background:#f0f0f0;color:#515154;font-weight:600;font-size:12px">{{ article.authorName?.[0] }}</el-avatar>
            <span style="font-size:13px;color:var(--text-secondary);font-weight:500">{{ article.authorName }}</span>
          </router-link>
        </div>

        <!-- TOC -->
        <div v-if="headings.length > 3" class="toc">
          <div class="toc-title">📑 目录</div>
          <div v-for="h in headings" :key="h.text" class="toc-item" :style="{ paddingLeft: (h.level - 1) * 12 + 'px' }" @click="scrollToHeading(h.text)">{{ h.text }}</div>
        </div>

        <img v-if="article.coverImage" :src="article.coverImage" loading="lazy" style="width:100%;border-radius:12px;margin-bottom:24px" />

        <div class="content markdown-body" :style="{ fontSize: fontSize + 'px' }" v-html="rendered" ref="contentRef"></div>

        <div class="font-size-ctrl">
          <el-button size="small" @click="fontSize = Math.max(14, fontSize - 2)">A-</el-button>
          <span style="font-size:12px;color:var(--text-secondary);margin:0 8px">{{ fontSize }}px</span>
          <el-button size="small" @click="fontSize = Math.min(24, fontSize + 2)">A+</el-button>
        </div>

        <div style="display:flex;gap:12px;margin:24px 0;align-items:center;flex-wrap:wrap">
          <el-button :type="liked ? 'danger' : 'default'" @click="handleLike" :icon="Star">{{ liked ? '已赞' : '点赞' }} {{ article.likesCount || 0 }}</el-button>
          <el-button @click="handleFollow" v-if="userStore.isLoggedIn && article.authorId !== user?.id">
            {{ followed ? '已关注' : '关注作者' }}
          </el-button>
          <el-button @click="copyLink">📋 分享</el-button>
        </div>

        <hr class="apple-divider" />
        <h3 style="font-size:17px;font-weight:600;margin-bottom:16px">评论 ({{ comments.length }})</h3>
        <div v-if="userStore.isLoggedIn" style="margin-bottom:20px">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评论..." style="margin-bottom:8px" />
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">发表评论</el-button>
        </div>
        <div v-else style="color:var(--text-secondary);margin:12px 0;font-size:14px">
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
    <button class="back-to-top" v-show="showTop" @click="window.scrollTo({top:0,behavior:'smooth'})">↑</button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getArticle, trackView, toggleLike, getComments, addComment, toggleFollow, checkFollow } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const article = ref(null)
const comments = ref([])
const newComment = ref('')
const liked = ref(false)
const followed = ref(false)
const fontSize = ref(16)
const progress = ref(0)
const showTop = ref(false)
const headings = ref([])
const contentRef = ref(null)
const user = JSON.parse(localStorage.getItem('user') || 'null')

// Markdown with highlight
marked.setOptions({
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) return hljs.highlight(code, { language: lang }).value
    return hljs.highlightAuto(code).value
  }
})

const rendered = computed(() => {
  if (!article.value?.content) return ''
  const html = marked(article.value.content)
  return html
})

function parseHeadings() {
  nextTick(() => {
    const el = contentRef.value
    if (!el) return
    const hs = el.querySelectorAll('h1, h2, h3')
    headings.value = Array.from(hs).map(h => ({ level: parseInt(h.tagName[1]), text: h.textContent }))
  })
}

function scrollToHeading(text) {
  const el = contentRef.value
  if (!el) return
  const hs = el.querySelectorAll('h1, h2, h3')
  for (const h of hs) {
    if (h.textContent === text) { h.scrollIntoView({ behavior: 'smooth' }); return }
  }
}

function onScroll() {
  const scrollTop = window.scrollY
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  progress.value = docHeight > 0 ? Math.min(100, (scrollTop / docHeight) * 100) : 0
  showTop.value = scrollTop > 300
}

async function fetchData() {
  try {
    const [a, c] = await Promise.all([getArticle(route.params.id), getComments(route.params.id)])
    article.value = a.data
    comments.value = c.data || []
    nextTick(() => { parseHeadings(); hljs.highlightAll() })
    trackView(route.params.id).catch(() => {})
    if (a.data) a.data.viewsCount = (a.data.viewsCount || 0) + 1
  } catch {}
  if (userStore.isLoggedIn && article.value?.authorId) {
    checkFollow(article.value.authorId).then(r => followed.value = r.data).catch(() => {})
  }
}

async function handleLike() {
  if (!user) { ElMessage.warning('请先登录'); return }
  try { const res = await toggleLike(route.params.id); liked.value = res.data; article.value.likesCount += (liked.value ? 1 : -1) } catch { ElMessage.error('操作失败') }
}

async function handleFollow() {
  if (!user) { ElMessage.warning('请先登录'); return }
  try { const res = await toggleFollow(article.value.authorId); followed.value = res.data; ElMessage.success(followed.value ? '已关注' : '已取消关注') } catch {} // eslint-disable-line
}

function copyLink() {
  navigator.clipboard.writeText(window.location.href).then(() => ElMessage.success('链接已复制')).catch(() => {})
}

async function submitComment() {
  if (!newComment.value.trim()) return
  try {
    await addComment({ articleId: article.value.id, content: newComment.value })
    ElMessage.success('评论成功'); newComment.value = ''
    const res = await getComments(route.params.id); comments.value = res.data || []
  } catch {}
}

onMounted(() => { fetchData(); window.addEventListener('scroll', onScroll) })
onUnmounted(() => { window.removeEventListener('scroll', onScroll) })
</script>

<style>
.reading-progress { position: fixed; top: 52px; left: 0; height: 3px; background: #0071e3; z-index: 1000; transition: width .1s; }
.toc { background: var(--card-bg); border-radius: 10px; padding: 14px 18px; margin-bottom: 20px; border: 1px solid var(--border-color); }
.toc-title { font-size: 14px; font-weight: 600; margin-bottom: 8px; }
.toc-item { font-size: 13px; color: #0071e3; cursor: pointer; padding: 3px 0; }
.toc-item:hover { text-decoration: underline; }
.font-size-ctrl { display: flex; align-items: center; justify-content: flex-end; margin: 16px 0; }
.back-to-top { position: fixed; right: 20px; bottom: 30px; width: 40px; height: 40px; border-radius: 50%; background: #0071e3; color: #fff; border: none; font-size: 20px; cursor: pointer; box-shadow: 0 2px 12px rgba(0,113,227,.3); z-index: 99; transition: all .2s; }
.back-to-top:hover { transform: scale(1.05); }
.markdown-body { font-size: 16px; line-height: 1.8; color: var(--text-color); }
.markdown-body h1, .markdown-body h2, .markdown-body h3 { margin-top: 1.5em; margin-bottom: 0.5em; }
.markdown-body p { margin-bottom: 1em; }
.markdown-body img { max-width: 100%; border-radius: 8px; }
.markdown-body code { background: #f5f5f7; padding: 2px 6px; border-radius: 4px; font-size: 14px; }
.markdown-body pre { background: #f5f5f7; padding: 16px; border-radius: 8px; overflow-x: auto; }
.markdown-body pre code { background: none; padding: 0; }
.markdown-body blockquote { border-left: 4px solid #0071e3; padding-left: 16px; color: var(--text-secondary); margin: 1em 0; }
.markdown-body ul, .markdown-body ol { padding-left: 24px; margin-bottom: 1em; }
</style>

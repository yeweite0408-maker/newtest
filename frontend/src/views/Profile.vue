<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width:720px;margin:0 auto">
      <div v-if="profile" class="profile-header" style="text-align:center;padding:32px 0">
        <div style="position:relative;display:inline-block">
          <el-avatar :size="72" :src="profile.user?.avatar" style="background:#f0f0f0;color:#515154;font-weight:700;font-size:28px;margin:0 auto 12px">{{ profile.user?.username?.[0]?.toUpperCase() }}</el-avatar>
          <el-upload v-if="isMe" class="avatar-upload" :auto-upload="false" :show-file-list="false" accept="image/*" @change="uploadAvatar">
            <el-button size="small" circle style="position:absolute;bottom:0;right:-4px;width:24px;height:24px;padding:0">📷</el-button>
          </el-upload>
        </div>
        <h1 style="font-size:24px;font-weight:700;margin-bottom:4px">{{ profile.user?.username }}</h1>
        <p style="color:var(--text-secondary);font-size:14px">{{ profile.user?.role === 'admin' ? '管理员' : '用户' }} · {{ profile.articles?.length || 0 }} 篇文章</p>
        <div v-if="profile.user?.bio" style="color:var(--text-secondary);font-size:14px;margin-top:4px">{{ profile.user.bio }}</div>
        <div v-if="isMe">
          <div style="margin-top:8px;display:flex;gap:8px;justify-content:center;flex-wrap:wrap">
            <el-input v-model="bioInput" placeholder="填写个人简介..." size="small" style="max-width:300px" @keyup.enter="saveBio" />
            <el-button size="small" @click="saveBio">保存简介</el-button>
            <router-link to="/settings"><el-button size="small">修改密码</el-button></router-link>
          </div>
        </div>
        <div v-else style="margin-top:12px">
          <el-button @click="handleFollow" size="small">{{ followed ? '已关注' : '关注' }}</el-button>
        </div>
        <div style="margin-top:8px;font-size:13px;color:var(--text-secondary)">
          关注 {{ followCount.followees || 0 }} · 粉丝 {{ followCount.followers || 0 }}
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
          <div class="card-header"><el-tag size="small">{{ article.category }}</el-tag><span class="date">{{ article.createdAt?.substring(0, 10) }}</span></div>
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
import { getUserProfile, getLikedArticles, updateBio, toggleFollow, checkFollow, getFollowCount, uploadImage } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const profile = ref(null)
const likedArticles = ref([])
const tab = ref('articles')
const user = JSON.parse(localStorage.getItem('user') || 'null')
const isMe = ref(Number(route.params.id) === user?.id)
const bioInput = ref('')
const followed = ref(false)
const followCount = ref({ followers: 0, followees: 0 })

onMounted(async () => {
  try {
    const [p, l, fc] = await Promise.all([
      getUserProfile(route.params.id),
      getLikedArticles(route.params.id),
      getFollowCount(route.params.id)
    ])
    profile.value = p.data
    likedArticles.value = l.data || []
    followCount.value = fc.data || {}
    bioInput.value = p.data?.user?.bio || ''
  } catch {}
  if (user && !isMe.value) {
    try { const r = await checkFollow(route.params.id); followed.value = r.data } catch {}
  }
})

async function saveBio() {
  try { await updateBio({ bio: bioInput.value }); ElMessage.success('简介已更新') } catch { ElMessage.error('保存失败') }
}

async function uploadAvatar(file) {
  try {
    const res = await uploadImage(file.raw)
    const avatarUrl = res.data.url
    if (profile.value) profile.value.user.avatar = avatarUrl
    ElMessage.success('头像已更新')
  } catch { ElMessage.error('上传失败') }
}

async function handleFollow() {
  try { const r = await toggleFollow(route.params.id); followed.value = r.data; ElMessage.success(followed.value ? '已关注' : '已取消关注'); const fc = await getFollowCount(route.params.id); followCount.value = fc.data || {} } catch {} // eslint-disable-line
}
</script>

<style scoped>
.avatar-upload { cursor: pointer; }
</style>

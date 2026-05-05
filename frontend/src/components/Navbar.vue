<template>
  <nav class="navbar">
    <router-link to="/" class="navbar-brand">
      <span class="logo-text">MyTest</span>
    </router-link>
    <div class="navbar-menu">
      <router-link to="/">
        <el-button text><el-icon><Reading /></el-icon><span>文章</span></el-button>
      </router-link>
      <router-link v-if="userStore.isLoggedIn" to="/messages">
        <el-button text>
          <el-icon><User /></el-icon><span>好友</span>
          <el-tag v-if="unreadCount > 0" size="small" type="danger" class="unread-badge">{{ unreadCount }}</el-tag>
        </el-button>
      </router-link>
      <router-link v-if="userStore.isLoggedIn" :to="'/profile/' + userStore.user?.id">
        <el-button text><el-icon><Memo /></el-icon><span>我的</span></el-button>
      </router-link>
      <router-link v-if="userStore.isAdmin" to="/admin">
        <el-button text><el-icon><Setting /></el-icon><span>管理</span></el-button>
      </router-link>

      <el-button text @click="toggleDark" style="font-size:16px">
        <el-icon><MoonNight v-if="!isDark" /><Sunny v-else /></el-icon>
      </el-button>

      <div v-if="userStore.isLoggedIn" class="navbar-user">
        <el-icon style="font-size:18px;color:#515154"><UserFilled /></el-icon>
        <span class="username">{{ userStore.username }}</span>
        <el-button text style="color:#86868b;font-size:12px" @click="handleLogout">退出</el-button>
      </div>
      <div v-else class="navbar-user">
        <router-link to="/login"><el-button text style="font-size:13px">登录</el-button></router-link>
        <router-link to="/register"><el-button type="primary" size="small" style="height:30px">注册</el-button></router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import { getUnreadCount, getNotificationUnread } from '../api'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)
const isDark = ref(document.documentElement.getAttribute('data-theme') === 'dark')
let timer = null

function toggleDark() {
  isDark.value = !isDark.value
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

async function fetchUnread() {
  if (!userStore.isLoggedIn) return
  try { const res = await getUnreadCount(); unreadCount.value = res.data.count } catch {}
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(() => { userStore.logout(); ElMessage.success('已退出'); router.push('/') }).catch(() => {})
}

onMounted(() => {
  const saved = localStorage.getItem('theme')
  if (saved) { isDark.value = saved === 'dark'; document.documentElement.setAttribute('data-theme', saved) }
  fetchUnread(); timer = setInterval(fetchUnread, 10000)
})
onUnmounted(() => { clearInterval(timer) })
</script>

<style scoped>
.unread-badge { margin-left: 4px; padding: 0 5px; height: 18px; line-height: 18px; border: none; }
</style>

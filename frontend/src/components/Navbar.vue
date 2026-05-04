<template>
  <nav class="navbar">
    <router-link to="/" class="navbar-brand">
      <span class="logo-icon">📝</span>
      <span class="logo-text">个人博客</span>
    </router-link>
    <div class="navbar-menu">
      <router-link to="/">
        <el-button text><el-icon><Reading /></el-icon> 文章</el-button>
      </router-link>
      <router-link v-if="userStore.isLoggedIn" to="/messages">
        <el-button text>
          <el-icon><Message /></el-icon> 消息
          <el-tag v-if="unreadCount > 0" size="small" type="danger" class="unread-badge">{{ unreadCount }}</el-tag>
        </el-button>
      </router-link>
      <router-link v-if="userStore.isAdmin" to="/admin">
        <el-button text><el-icon><Setting /></el-icon> 管理</el-button>
      </router-link>
      <div v-if="userStore.isLoggedIn" class="navbar-user">
        <el-icon class="avatar-icon"><UserFilled /></el-icon>
        <span class="username">{{ userStore.username }}</span>
        <el-button text style="color:#999" @click="handleLogout">退出</el-button>
      </div>
      <div v-else class="navbar-user">
        <router-link to="/login"><el-button text>登录</el-button></router-link>
        <router-link to="/register"><el-button type="primary" size="small">注册</el-button></router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import { getUnreadCount } from '../api'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

async function fetchUnread() {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data.count
  } catch {}
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出')
    router.push('/')
  }).catch(() => {})
}

onMounted(fetchUnread)
</script>

<style scoped>
.unread-badge { margin-left: 4px; }
</style>

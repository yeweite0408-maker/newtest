<template>
  <nav class="navbar">
    <router-link to="/" class="navbar-brand">
      <span class="logo-icon">🎮</span>
      <span class="logo-text">GameMall</span>
    </router-link>
    <div class="navbar-menu">
      <router-link to="/">
        <el-button text><el-icon><Goods /></el-icon> 游戏商城</el-button>
      </router-link>
      <router-link to="/cart">
        <el-button text><el-icon><ShoppingCart /></el-icon> 购物车</el-button>
      </router-link>
      <router-link v-if="userStore.isAdmin" to="/admin">
        <el-button text><el-icon><Setting /></el-icon> 后台管理</el-button>
      </router-link>
      <div class="navbar-user">
        <el-icon class="avatar-icon"><UserFilled /></el-icon>
        <span class="username">{{ userStore.username }}</span>
        <el-button text style="color:#999" @click="handleLogout">退出</el-button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

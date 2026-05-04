import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/article/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue') },
  { path: '/messages', name: 'Messages', component: () => import('../views/Messages.vue'), meta: { requiresAuth: true } },
  { path: '/profile/:id', name: 'Profile', component: () => import('../views/Profile.vue') },
  { path: '/settings', name: 'Settings', component: () => import('../views/Settings.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录')
    next('/login')
  } else if (to.meta.requiresAdmin && user?.role !== 'admin') {
    ElMessage.error('需要管理员权限')
    next('/')
  } else if (to.meta.guest && token) {
    next('/')
  } else {
    next()
  }
})

export default router

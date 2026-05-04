import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    }
    return Promise.reject(error)
  }
)

export function login(data) { return api.post('/auth/login', data) }
export function register(data) { return api.post('/auth/register', data) }

// Articles
export function getArticles() { return api.get('/articles') }
export function getArticle(id) { return api.get(`/articles/${id}`) }
export function searchArticles(keyword) { return api.get('/articles/search', { params: { keyword } }) }
export function getArticlesByCategory(category) { return api.get(`/articles/category/${category}`) }
export function createArticle(data) { return api.post('/articles', data) }
export function updateArticle(id, data) { return api.put(`/articles/${id}`, data) }
export function deleteArticle(id) { return api.delete(`/articles/${id}`) }

// Comments
export function getComments(articleId) { return api.get(`/comments/article/${articleId}`) }
export function addComment(data) { return api.post('/comments', data) }
export function deleteComment(id) { return api.delete(`/comments/${id}`) }

// Messages
export function getMessages() { return api.get('/messages') }
export function getConversation(userId) { return api.get(`/messages/conversation/${userId}`) }
export function sendMessage(data) { return api.post('/messages', data) }
export function getUnreadCount() { return api.get('/messages/unread') }
export function markRead() { return api.put('/messages/read') }

// Upload
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Admin
export function getUsers() { return api.get('/admin/users') }
export function getAdminArticles() { return api.get('/admin/articles') }
export function createAdminArticle(data) { return api.post('/admin/articles', data) }
export function updateAdminArticle(id, data) { return api.put(`/admin/articles/${id}`, data) }
export function deleteAdminArticle(id) { return api.delete(`/admin/articles/${id}`) }

export default api

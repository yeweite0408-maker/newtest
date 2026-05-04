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

export function login(data) {
  return api.post('/auth/login', data)
}

export function register(data) {
  return api.post('/auth/register', data)
}

export function getProducts(keyword) {
  return api.get('/products', { params: { keyword } })
}

export function getProduct(id) {
  return api.get(`/products/${id}`)
}

export function getCart() {
  return api.get('/cart')
}

export function addToCart(productId, quantity) {
  return api.post('/cart', { productId, quantity })
}

export function updateCart(id, quantity) {
  return api.put(`/cart/${id}`, { quantity })
}

export function removeCart(id) {
  return api.delete(`/cart/${id}`)
}

export function clearCart() {
  return api.delete('/cart')
}

export function getUsers() {
  return api.get('/admin/users')
}

export function getAdminProducts() {
  return api.get('/admin/products')
}

export function addProduct(data) {
  return api.post('/admin/products', data)
}

export function updateProduct(id, data) {
  return api.put(`/admin/products/${id}`, data)
}

export function deleteProduct(id) {
  return api.delete(`/admin/products/${id}`)
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export default api

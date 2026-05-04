import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'admin')
  const username = computed(() => user.value?.username || '')

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function setToken(token) {
    localStorage.setItem('token', token)
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  function getToken() {
    return localStorage.getItem('token')
  }

  return { user, isLoggedIn, isAdmin, username, setUser, setToken, logout, getToken }
})

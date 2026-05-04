<template>
  <div>
    <Navbar />
    <div class="page-container">
      <div style="display:flex;align-items:center;gap:10px;margin-bottom:20px">
        <h1 style="font-size:24px;font-weight:700;margin:0;letter-spacing:-0.3px">好友</h1>
        <el-input v-model="searchQuery" placeholder="搜索用户..." clearable size="small" style="max-width:200px" @input="searchUsers" />
      </div>

      <div v-if="!currentChat" class="friend-list">
        <div v-for="u in filteredUsers" :key="u.id" class="friend-item" @click="startChat(u)">
          <el-avatar :size="40" style="background:#f0f0f0;color:#515154;font-weight:600">
            {{ u.username?.[0]?.toUpperCase() }}
          </el-avatar>
          <div style="flex:1">
            <div class="friend-name">{{ u.username }}</div>
            <div style="font-size:12px;color:#86868b">{{ u.role === 'admin' ? '管理员' : '用户' }}</div>
          </div>
          <el-icon style="color:#c7c7cc"><ArrowRight /></el-icon>
        </div>
        <el-empty v-if="filteredUsers.length === 0" description="暂无用户" :image-size="60" style="padding:40px 0" />
      </div>

      <div v-else class="chat-box">
        <div class="chat-header">
          <span class="back-btn" @click="currentChat = null">← 返回</span>
          <span style="margin-left:12px">{{ chatUsername }}</span>
        </div>
        <div class="chat-messages" ref="msgBox">
          <div v-for="msg in messages" :key="msg.id" class="msg" :class="{ mine: msg.fromUserId === myId }">
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-time">{{ msg.createdAt?.substring(11, 16) }}</div>
          </div>
          <div v-if="messages.length === 0" style="text-align:center;color:#86868b;padding:40px 0;font-size:14px">暂无消息，发送第一条消息吧</div>
        </div>
        <div class="chat-input">
          <el-input v-model="chatInput" placeholder="输入消息..." @keyup.enter="sendMsg" clearable />
          <el-button type="primary" @click="sendMsg" :disabled="!chatInput.trim()">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getAllUsers, getConversation, sendMessage as apiSend } from '../api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const users = ref([])
const searchQuery = ref('')
const messages = ref([])
const currentChat = ref(null)
const chatUsername = ref('')
const chatInput = ref('')
const myId = ref(null)
const msgBox = ref(null)

const filteredUsers = computed(() => {
  let list = users.value.filter(u => u.id !== myId.value)
  if (searchQuery.value) {
    list = list.filter(u => u.username.includes(searchQuery.value))
  }
  return list
})

async function fetchUsers() {
  try { const res = await getAllUsers(); users.value = res.data || [] } catch {}
}

async function startChat(user) {
  currentChat.value = user.id
  chatUsername.value = user.username
  try {
    const res = await getConversation(user.id)
    messages.value = res.data || []
    await nextTick()
    msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' })
  } catch {}
}

async function sendMsg() {
  if (!chatInput.value.trim()) return
  try {
    await apiSend({ toUserId: currentChat.value, content: chatInput.value })
    chatInput.value = ''
    const res = await getConversation(currentChat.value)
    messages.value = res.data || []
    await nextTick()
    msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' })
  } catch { ElMessage.error('发送失败') }
}

function searchUsers() {}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  myId.value = user.id
  fetchUsers()
})
</script>

<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width:860px;margin:0 auto;padding:20px">
      <div style="display:flex;align-items:center;gap:12px;margin-bottom:20px">
        <h1 style="font-size:22px;font-weight:700;margin:0;letter-spacing:-0.3px">👥 好友</h1>
        <el-input v-model="searchQuery" placeholder="搜索用户..." clearable size="small" style="max-width:200px" @input="searchUsers" :prefix-icon="Search" />
      </div>

      <div class="chat-layout">
        <!-- 好友列表 -->
        <div class="user-panel" v-show="!currentChat || !isMobile">
          <div v-for="u in filteredUsers" :key="u.id" class="user-card" :class="{ active: currentChat === u.id }" @click="startChat(u)">
            <div class="user-avatar" :style="{ background: avatarColor(u.username) }">
              {{ u.username?.[0]?.toUpperCase() }}
            </div>
            <div class="user-meta">
              <div class="user-name">{{ u.username }}</div>
              <div class="user-badge">{{ u.role === 'admin' ? '管理员' : '用户' }}</div>
            </div>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
          <el-empty v-if="filteredUsers.length === 0" description="暂无用户" :image-size="50" style="padding:30px 0" />
        </div>

        <!-- 聊天区域 -->
        <div class="chat-panel" v-show="currentChat">
          <div v-if="currentChat" class="chat-container">
            <div class="chat-topbar">
              <button class="mobile-back" @click="currentChat = null">←</button>
              <div class="chat-avatar" :style="{ background: avatarColor(chatUsername) }">
                {{ chatUsername?.[0]?.toUpperCase() }}
              </div>
              <span class="chat-partner">{{ chatUsername }}</span>
            </div>
            <div class="chat-body" ref="msgBox">
              <div v-for="msg in messages" :key="msg.id" class="chat-bubble-row" :class="{ 'is-mine': msg.fromUserId === myId }">
                <div class="chat-bubble">{{ msg.content }}</div>
                <div class="chat-time">{{ msg.createdAt?.substring(11, 16) }}</div>
              </div>
              <div v-if="messages.length === 0" class="chat-empty">发送第一条消息吧</div>
            </div>
            <div class="chat-bottom">
              <el-input v-model="chatInput" placeholder="输入消息..." @keyup.enter="sendMsg" clearable :disabled="!currentChat" />
              <el-button type="primary" @click="sendMsg" :disabled="!chatInput.trim() || !currentChat" style="min-width:72px">发送</el-button>
            </div>
          </div>
        </div>

        <!-- 未选择时 -->
        <div class="chat-placeholder" v-show="!currentChat && isMobile">
          <div class="placeholder-icon">💬</div>
          <div class="placeholder-text">选择一个好友开始聊天</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getAllUsers, getConversation, sendMessage as apiSend } from '../api'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const users = ref([])
const searchQuery = ref('')
const messages = ref([])
const currentChat = ref(null)
const chatUsername = ref('')
const chatInput = ref('')
const myId = ref(null)
const msgBox = ref(null)
const isMobile = ref(window.innerWidth <= 768)

window.addEventListener('resize', () => { isMobile.value = window.innerWidth <= 768 })

const filteredUsers = computed(() => {
  let list = users.value.filter(u => u.id !== myId.value)
  if (searchQuery.value) list = list.filter(u => u.username.includes(searchQuery.value))
  return list
})

const colors = ['#0071e3','#34c759','#ff9500','#ff3b30','#5856d6','#af52de','#5ac8fa','#ff2d55']
function avatarColor(name) {
  let i = 0; for (const c of (name||'')) i += c.charCodeAt(0); return colors[i % colors.length]
}

async function fetchUsers() {
  try { const res = await getAllUsers(); users.value = res.data || [] } catch {}
}

let pollTimer = null
function startPolling() {
  if (pollTimer) clearInterval(pollTimer)
  pollTimer = setInterval(async () => {
    if (!currentChat.value) return
    try {
      const res = await getConversation(currentChat.value)
      const newMsgs = res.data || []
      const hadNew = newMsgs.length > messages.value.length
      messages.value = newMsgs
      if (hadNew) await nextTick(() => msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' }))
    } catch {}
  }, 3000)
}
function stopPolling() { if (pollTimer) { clearInterval(pollTimer); pollTimer = null } }

async function startChat(user) {
  stopPolling()
  currentChat.value = user.id
  chatUsername.value = user.username
  try {
    const res = await getConversation(user.id)
    messages.value = res.data || []
    await nextTick()
    msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight })
  } catch {}
  startPolling()
}

async function sendMsg() {
  if (!chatInput.value.trim() || !currentChat.value) return
  const text = chatInput.value
  chatInput.value = ''
  try {
    await apiSend({ toUserId: currentChat.value, content: text })
    const res = await getConversation(currentChat.value)
    messages.value = res.data || []
    await nextTick()
    msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' })
  } catch { ElMessage.error('发送失败'); chatInput.value = text }
}

function searchUsers() {}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  myId.value = user.id
  fetchUsers()
})
onUnmounted(stopPolling)
</script>

<style scoped>
.chat-layout {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0,0,0,.04);
  min-height: 520px;
}
.user-panel {
  width: 280px;
  border-right: 1px solid #f0f0f0;
  overflow-y: auto;
  flex-shrink: 0;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all .15s;
  border-bottom: 1px solid #f5f5f7;
}
.user-card:hover { background: #f5f5f7; }
.user-card.active { background: #f0f0f0; }
.user-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-weight: 700; font-size: 16px;
  flex-shrink: 0;
}
.user-meta { flex: 1; min-width: 0; }
.user-name { font-size: 14px; font-weight: 600; color: #1d1d1f; }
.user-badge { font-size: 12px; color: #86868b; margin-top: 2px; }
.arrow { color: #c7c7cc; font-size: 16px; }
.chat-panel { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-container { display: flex; flex-direction: column; height: 520px; }
.chat-topbar {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 16px; border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}
.mobile-back { display: none; background: none; border: none; font-size: 20px; cursor: pointer; color: #0071e3; padding: 0 4px; }
.chat-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-weight: 600; font-size: 13px;
}
.chat-partner { font-size: 14px; font-weight: 600; color: #1d1d1f; }
.chat-body { flex: 1; overflow-y: auto; padding: 16px; background: #fafafa; }
.chat-bubble-row { margin-bottom: 14px; max-width: 75%; }
.chat-bubble-row.is-mine { margin-left: auto; text-align: right; }
.chat-bubble {
  display: inline-block; padding: 10px 16px; border-radius: 18px;
  font-size: 14px; line-height: 1.45; text-align: left;
  background: #f0f0f0; color: #1d1d1f;
  border-bottom-left-radius: 4px;
}
.is-mine .chat-bubble {
  background: #0071e3; color: #fff;
  border-bottom-left-radius: 18px; border-bottom-right-radius: 4px;
}
.chat-time { font-size: 11px; color: #86868b; margin-top: 4px; }
.chat-empty { text-align: center; color: #86868b; padding: 60px 0; font-size: 14px; }
.chat-bottom { display: flex; gap: 8px; padding: 12px 16px; border-top: 1px solid #f0f0f0; background: #fff; }
.chat-bottom .el-input__wrapper { border-radius: 10px !important; }
.chat-placeholder {
  flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: #c7c7cc; min-height: 520px;
}
.placeholder-icon { font-size: 48px; margin-bottom: 12px; }
.placeholder-text { font-size: 15px; }

@media (max-width: 768px) {
  .chat-layout { flex-direction: column; border-radius: 12px; min-height: auto; }
  .user-panel { width: 100%; border-right: none; max-height: 400px; }
  .chat-container { height: calc(100vh - 160px); }
  .mobile-back { display: inline-block; }
  .chat-placeholder { display: none !important; }
  .chat-bubble-row { max-width: 85%; }
}
</style>

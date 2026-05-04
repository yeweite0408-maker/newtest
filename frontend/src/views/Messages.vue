<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width: 600px; margin: 0 auto;">
      <h1>我的消息</h1>

      <div v-if="conversations.length > 0" class="user-list">
        <div v-for="conv in conversations" :key="conv.userId" class="user-item" :class="{ active: currentChat === conv.userId }" @click="openChat(conv.userId, conv.username)">
          <el-avatar :size="36">{{ conv.username?.[0] }}</el-avatar>
          <div class="user-info">
            <span class="user-name">{{ conv.username }}</span>
          </div>
        </div>
      </div>

      <div v-if="currentChat" class="chat-box">
        <div class="chat-header">与 {{ chatUsername }} 的对话</div>
        <div class="chat-messages" ref="msgBox">
          <div v-for="msg in messages" :key="msg.id" class="msg" :class="{ mine: msg.fromUserId === myId }">
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-time">{{ msg.createdAt?.substring(11, 16) }}</div>
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="chatInput" placeholder="输入消息..." @keyup.enter="sendMsg" />
          <el-button type="primary" @click="sendMsg">发送</el-button>
        </div>
      </div>

      <el-empty v-else-if="!loading" description="暂无消息，在文章评论中与作者互动吧" />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import { useUserStore } from '../stores/user'
import { getMessages, sendMessage, getConversation } from '../api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const conversations = ref([])
const messages = ref([])
const currentChat = ref(null)
const chatUsername = ref('')
const chatInput = ref('')
const myId = ref(null)
const msgBox = ref(null)
const loading = ref(true)

async function fetchMessages() {
  try {
    const res = await getMessages()
    const all = res.data || []
    const userMap = {}
    all.forEach(m => {
      const otherId = m.fromUserId === myId.value ? m.toUserId : m.fromUserId
      const otherName = m.fromUserId === myId.value ? m.toUsername : m.fromUsername
      if (!userMap[otherId]) {
        userMap[otherId] = { userId: otherId, username: otherName, lastMsg: m.content, lastTime: m.createdAt }
      }
    })
    conversations.value = Object.values(userMap)
  } catch {}
  loading.value = false
}

async function openChat(userId, username) {
  currentChat.value = userId
  chatUsername.value = username
  try {
    const res = await getConversation(userId)
    messages.value = res.data || []
    await nextTick()
    msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' })
  } catch {}
}

async function sendMsg() {
  if (!chatInput.value.trim()) return
  try {
    await sendMessage({ toUserId: currentChat.value, content: chatInput.value })
    chatInput.value = ''
    await openChat(currentChat.value, chatUsername.value)
  } catch { ElMessage.error('发送失败') }
}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  myId.value = user.id
  fetchMessages()
})
</script>

<style scoped>
.user-list { margin-bottom: 16px; }
.user-item { display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 8px; cursor: pointer; border: 1px solid #eee; margin-bottom: 8px; }
.user-item:hover, .user-item.active { background: #f5f7fa; }
.user-name { font-weight: 500; }
.chat-box { border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; }
.chat-header { padding: 12px 16px; background: #f5f7fa; font-weight: 500; border-bottom: 1px solid #e0e0e0; }
.chat-messages { height: 300px; overflow-y: auto; padding: 16px; }
.msg { margin-bottom: 12px; max-width: 70%; }
.msg.mine { margin-left: auto; }
.msg-content { background: #f0f0f0; padding: 8px 12px; border-radius: 8px; display: inline-block; }
.msg.mine .msg-content { background: #409eff; color: #fff; }
.msg-time { font-size: 12px; color: #999; margin-top: 4px; }
.msg.mine .msg-time { text-align: right; }
.chat-input { display: flex; gap: 8px; padding: 12px; border-top: 1px solid #e0e0e0; }
</style>

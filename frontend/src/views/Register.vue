<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-icon">🎮</div>
        <h2>创建账号</h2>
        <p>加入 GameMall 游戏商城</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱（选填）" prefix-icon="Message" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <div v-if="error" style="color:#e94560;text-align:center;margin-bottom:12px;font-size:14px">{{ error }}</div>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/index'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const error = ref('')

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  error.value = ''
  try {
    const res = await register({ username: form.username, password: form.password, email: form.email })
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUser(res.data.user)
      ElMessage.success('注册成功')
      router.push('/')
    } else {
      error.value = res.msg
    }
  } catch (e) {
    error.value = '注册失败，请检查网络连接'
  } finally {
    loading.value = false
  }
}
</script>

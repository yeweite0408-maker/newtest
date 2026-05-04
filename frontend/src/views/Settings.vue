<template>
  <div>
    <Navbar />
    <div class="page-container" style="max-width:480px;margin:0 auto;padding-top:40px">
      <div class="auth-card" style="margin:0 auto">
        <div class="auth-header">
          <h2>修改密码</h2>
          <p>设置新密码</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleSubmit">
          <el-form-item prop="oldPassword">
            <el-input v-model="form.oldPassword" type="password" placeholder="原密码" show-password />
          </el-form-item>
          <el-form-item prop="newPassword">
            <el-input v-model="form.newPassword" type="password" placeholder="新密码（至少6位）" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit" style="width:100%">确认修改</el-button>
          </el-form-item>
        </el-form>
        <div v-if="msg" :style="{color: msgType === 'ok' ? '#34c759' : '#ff3b30', textAlign:'center', fontSize:'14px'}">{{ msg }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { changePassword } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const msg = ref('')
const msgType = ref('')
const form = reactive({ oldPassword: '', newPassword: '' })
const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }]
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true; msg.value = ''
  try {
    await changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    msg.value = '密码修改成功'; msgType.value = 'ok'
    ElMessage.success('密码已更新')
    setTimeout(() => router.push('/'), 1500)
  } catch (e) {
    msg.value = e.response?.data?.msg || '修改失败'; msgType.value = 'error'
  } finally { loading.value = false }
}
</script>

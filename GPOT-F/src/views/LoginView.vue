<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="title">GPOT 快递管理系统</h1>

      <!-- 用户类型选择 -->
      <div class="user-type-selector">
        <button
          v-for="type in userTypes"
          :key="type.value"
          :class="['type-btn', { active: selectedUserType === type.value }]"
          @click="selectedUserType = type.value"
          :disabled="type.value !== 'user'"
        >
          {{ type.label }}
          <span v-if="type.value !== 'user'" class="coming-soon">即将推出</span>
        </button>
      </div>

      <!-- 登录表单 -->
      <div v-if="!isRegisterMode" class="form-section">
        <h2>用户登录</h2>
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              id="username"
              v-model="loginForm.username"
              type="text"
              required
              placeholder="请输入用户名"
            />
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              v-model="loginForm.password"
              type="password"
              required
              placeholder="请输入密码"
            />
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>

        <div class="switch-mode">
          还没有账号？
          <button @click="isRegisterMode = true" class="link-btn">立即注册</button>
        </div>
      </div>

      <!-- 注册表单 -->
      <div v-else class="form-section">
        <h2>用户注册</h2>
        <form @submit.prevent="handleRegister" class="login-form">
          <div class="form-group">
            <label for="registerUsername">用户名</label>
            <input
              id="registerUsername"
              v-model="registerForm.username"
              type="text"
              required
              placeholder="请输入用户名"
            />
          </div>

          <div class="form-group">
            <label for="registerPassword">密码</label>
            <input
              id="registerPassword"
              v-model="registerForm.password"
              type="password"
              required
              placeholder="请输入密码"
            />
          </div>

          <div class="form-group">
            <label for="realName">真实姓名</label>
            <input
              id="realName"
              v-model="registerForm.realName"
              type="text"
              required
              placeholder="请输入真实姓名"
            />
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <div class="switch-mode">
          已有账号？
          <button @click="isRegisterMode = false" class="link-btn">返回登录</button>
        </div>
      </div>

      <!-- 错误信息显示 -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- 注册成功弹窗 -->
      <div v-if="showRegisterSuccess" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
          <h3>注册成功！</h3>
          <p>您的账号已成功注册，请点击下方按钮返回登录页面。</p>
          <button @click="handleBackToLogin" class="modal-btn">返回登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()

    const selectedUserType = ref('user')
    const isRegisterMode = ref(false)
    const loading = ref(false)
    const errorMessage = ref('')
    const showRegisterSuccess = ref(false)

    const userTypes = [
      { value: 'admin', label: '管理员' },
      { value: 'employee', label: '员工' },
      { value: 'user', label: '用户' }
    ]

    const loginForm = ref({
      username: '',
      password: ''
    })

    const registerForm = ref({
      username: '',
      password: '',
      realName: ''
    })

    const handleLogin = async () => {
      if (selectedUserType.value !== 'user') {
        errorMessage.value = '目前只支持用户登录'
        return
      }

      loading.value = true
      errorMessage.value = ''

      try {
        const response = await api.login({
          userType: selectedUserType.value,
          username: loginForm.value.username,
          password: loginForm.value.password
        })

        if (response.data.success) {
          // 登录成功，保存用户信息到localStorage
          const userInfo = {
            id: response.data.data.userId,
            username: response.data.data.username,
            realName: response.data.data.realName,
            userType: response.data.data.userType
          }
          localStorage.setItem('user', JSON.stringify(userInfo))

          // 跳转到主页面
          router.push('/main/send-package')
        } else {
          errorMessage.value = response.data.message
        }
      } catch (error) {
        console.error('登录失败:', error)
        errorMessage.value = error.response?.data?.message || '登录失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    const handleRegister = async () => {
      loading.value = true
      errorMessage.value = ''

      try {
        const response = await api.register({
          username: registerForm.value.username,
          password: registerForm.value.password,
          realName: registerForm.value.realName
        })

        if (response.data.success) {
          // 注册成功，显示弹窗
          showRegisterSuccess.value = true
        } else {
          errorMessage.value = response.data.message
        }
      } catch (error) {
        console.error('注册失败:', error)
        errorMessage.value = error.response?.data?.message || '注册失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    const handleBackToLogin = () => {
      showRegisterSuccess.value = false
      isRegisterMode.value = false
      registerForm.value = { username: '', password: '', realName: '' }
      errorMessage.value = ''
    }

    const closeModal = () => {
      // 不允许点击遮罩关闭弹窗，必须点击按钮
    }

    return {
      selectedUserType,
      isRegisterMode,
      loading,
      errorMessage,
      showRegisterSuccess,
      userTypes,
      loginForm,
      registerForm,
      handleLogin,
      handleRegister,
      handleBackToLogin,
      closeModal
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.user-type-selector {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.type-btn {
  flex: 1;
  padding: 12px;
  border: 2px solid #e1e5e9;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

.type-btn:hover:not(:disabled) {
  border-color: #667eea;
}

.type-btn.active {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.type-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.coming-soon {
  display: block;
  font-size: 10px;
  opacity: 0.8;
  margin-top: 2px;
}

.form-section h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
  font-size: 20px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.form-group input {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.submit-btn {
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.switch-mode {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.link-btn {
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-weight: 500;
  text-decoration: underline;
}

.link-btn:hover {
  color: #5a67d8;
}

.error-message {
  margin-top: 20px;
  padding: 12px;
  background: #fee;
  color: #c33;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #fcc;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 300px;
  width: 90%;
}

.modal-content h3 {
  color: #28a745;
  margin-bottom: 15px;
  font-size: 20px;
}

.modal-content p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.modal-btn {
  padding: 12px 24px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modal-btn:hover {
  background: #218838;
}
</style>
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title-bar">
        <h1 class="title">GPOT 快递管理系统</h1>
        <button class="debug-link" @click="goDebug">Debug</button>
      </div>

      <!-- 用户类型选择 -->
      <div class="user-type-selector">
        <button
          v-for="type in userTypes"
          :key="type.value"
          :class="['type-btn', { active: selectedUserType === type.value }]"
          @click="selectedUserType = type.value"
        >
          {{ type.label }}
        </button>
      </div>

      <!-- 登录表单 -->
      <div v-if="!isRegisterMode" class="form-section">
        <h2>{{ getLoginTitle() }}</h2>
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
          const userInfoData = response.data.data.userInfo
          const userInfo = {
            id: response.data.data.userId,
            username: response.data.data.username,
            realName: response.data.data.realName,
            userType: response.data.data.userType,
            // 员工不再区分A/B：统一按B处理，避免前端出现A分支
            department: selectedUserType.value === 'employee' ? 'B' : (userInfoData ? userInfoData.department : null)
          }
          localStorage.setItem('user', JSON.stringify(userInfo))

          // 根据用户类型和员工部门跳转到不同页面
          if (selectedUserType.value === 'employee') {
            // 员工登录，统一跳转到员工布局页面
            router.push('/employee/profile')
          } else if (selectedUserType.value === 'admin') {
            router.push('/admin/dashboard')
          } else {
            // 普通用户跳转到“我的包裹”
            router.push('/main/packages')
          }
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

    // 获取登录标题
    const getLoginTitle = () => {
      const titles = {
        admin: '管理员登录',
        employee: '员工登录',
        user: '用户登录'
      }
      return titles[selectedUserType.value] || '登录'
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

    const goDebug = () => {
      router.push('/debug')
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
      closeModal,
      getLoginTitle,
      goDebug
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
  background: linear-gradient(135deg, #F5F5F5 0%, #E8E8E8 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 48px;
  width: 100%;
  max-width: 420px;
  border: 3px solid #DC143C;
}

.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title {
  color: #333333;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  border-bottom: 3px solid #DC143C;
  padding-bottom: 8px;
}

.debug-link {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: #fff;
  cursor: pointer;
  color: #666;
}

.debug-link:hover {
  border-color: #DC143C;
  color: #DC143C;
}

.user-type-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 36px;
}

.type-btn {
  flex: 1;
  padding: 14px;
  border: 2px solid #cccccc;
  background: white;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 600;
  position: relative;
  letter-spacing: 0.5px;
}

.type-btn:hover:not(:disabled) {
  border-color: #DC143C;
}

.type-btn.active {
  border-color: #DC143C;
  background: #DC143C;
  color: white;
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
  padding: 14px 16px;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 16px;
  transition: border-color 0.2s ease;
  background: #ffffff;
}

.form-group input:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.1);
}

.submit-btn {
  padding: 16px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.submit-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
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
  color: #DC143C;
  cursor: pointer;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s ease;
}

.link-btn:hover {
  color: #B22222;
  text-decoration: underline;
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
  padding: 36px;
  border-radius: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 320px;
  width: 90%;
  border: 2px solid #DC143C;
}

.modal-content h3 {
  color: #333333;
  margin-bottom: 18px;
  font-size: 22px;
  font-weight: 700;
}

.modal-content p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.modal-btn {
  padding: 14px 28px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.modal-btn:hover {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}
</style>
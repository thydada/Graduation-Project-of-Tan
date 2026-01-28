<template>
  <div class="profile-container">
    <div class="page-header">
      <h1>个人信息管理</h1>
    </div>

    <div class="profile-card">
      <div class="profile-section">
        <h2 class="section-title">基本信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <label class="info-label">用户名：</label>
            <span class="info-value">{{ userInfo.username || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="info-label">真实姓名：</label>
            <span class="info-value">{{ userInfo.realName || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="info-label">员工ID：</label>
            <span class="info-value">{{ userInfo.id || '-' }}</span>
          </div>
          <div class="info-item">
            <label class="info-label">部门：</label>
            <span class="info-value">{{ userInfo.department || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="profile-section">
        <h2 class="section-title">账户信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <label class="info-label">用户类型：</label>
            <span class="info-value">员工</span>
          </div>
          <div class="info-item">
            <label class="info-label">登录状态：</label>
            <span class="info-value status-active">已登录</span>
          </div>
        </div>
      </div>

      <div class="profile-actions">
        <button class="btn btn-primary" @click="refreshInfo">
          刷新信息
        </button>
      </div>
    </div>

    <!-- 成功提示 -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'EmployeeProfileView',
  setup() {
    const userInfo = ref({})
    const successMessage = ref('')
    const errorMessage = ref('')

    const loadUserInfo = () => {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        userInfo.value = user
      } catch (error) {
        console.error('加载用户信息失败:', error)
        errorMessage.value = '加载用户信息失败'
      }
    }

    const refreshInfo = () => {
      loadUserInfo()
      successMessage.value = '信息已刷新'
      setTimeout(() => {
        successMessage.value = ''
      }, 2000)
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      userInfo,
      successMessage,
      errorMessage,
      refreshInfo
    }
  }
}
</script>

<style scoped>
.profile-container {
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.profile-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 32px;
}

.profile-section {
  margin-bottom: 32px;
}

.profile-section:last-of-type {
  margin-bottom: 0;
}

.section-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #DC143C;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
}

.info-label {
  font-weight: 500;
  color: #666;
  min-width: 100px;
  margin-right: 12px;
}

.info-value {
  color: #333;
  font-size: 16px;
}

.status-active {
  color: #28a745;
  font-weight: 600;
}

.profile-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
}

.btn {
  padding: 12px 24px;
  border: 2px solid;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.btn-primary {
  background: #DC143C;
  color: white;
  border-color: #DC143C;
}

.btn-primary:hover {
  background: #B22222;
  border-color: #B22222;
}

.success-message {
  margin-top: 20px;
  padding: 14px;
  background: #d4edda;
  color: #155724;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #c3e6cb;
}

.error-message {
  margin-top: 20px;
  padding: 14px;
  background: #fee;
  color: #c33;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #fcc;
}
</style>

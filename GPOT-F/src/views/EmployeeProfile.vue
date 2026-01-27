<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar">
            {{ userInfo.realName ? userInfo.realName.charAt(0).toUpperCase() : 'E' }}
          </div>
          <h2 class="user-name">{{ userInfo.realName || '未知' }}</h2>
          <span class="user-role">{{ getRoleText(userInfo.department) }}</span>
        </div>
      </div>

      <div class="profile-body">
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>员工ID</label>
              <span class="info-value">{{ userInfo.id || '-' }}</span>
            </div>
            <div class="info-item">
              <label>用户名</label>
              <span class="info-value">{{ userInfo.username || '-' }}</span>
            </div>
            <div class="info-item">
              <label>真实姓名</label>
              <span class="info-value">{{ userInfo.realName || '-' }}</span>
            </div>
            <div class="info-item">
              <label>所属部门</label>
              <span class="info-value">{{ getDepartmentText(userInfo.department) }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h3 class="section-title">系统信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>账号状态</label>
              <span class="status-active">正常</span>
            </div>
            <div class="info-item">
              <label>用户类型</label>
              <span class="info-value">员工</span>
            </div>
            <div class="info-item">
              <label>系统版本</label>
              <span class="info-value">GPOT v1.0</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'EmployeeProfile',
  setup() {
    const userInfo = ref({
      id: null,
      username: '',
      realName: '',
      department: '',
      userType: 'employee'
    })

    // 获取角色文本
    const getRoleText = (dept) => {
      const roleMap = {
        'A': '取件审核员',
        'B': '核验入库员'
      }
      return roleMap[dept] || '普通员工'
    }

    // 获取部门文本
    const getDepartmentText = (dept) => {
      const deptMap = {
        'A': '部门 A - 取件审核',
        'B': '部门 B - 核验入库'
      }
      return deptMap[dept] || (dept || '未分配')
    }

    // 页面加载时获取用户信息
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      userInfo.value = JSON.parse(storedUser)
    }

    return {
      userInfo,
      getRoleText,
      getDepartmentText
    }
  }
}
</script>

<style scoped>
.profile-container {
  min-height: 100%;
  padding: 20px;
}

.profile-card {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  background: linear-gradient(135deg, #B22222 0%, #8B0000 100%);
  padding: 40px;
  text-align: center;
  color: white;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: white;
  color: #B22222;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 16px;
  border: 4px solid rgba(255, 255, 255, 0.3);
}

.user-name {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
}

.user-role {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.profile-body {
  padding: 32px 40px;
}

.info-section {
  margin-bottom: 32px;
}

.info-section:last-of-type {
  margin-bottom: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #DC143C;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item label {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  padding: 8px 12px;
  background: #f8f8f8;
  border-radius: 4px;
}

.status-active {
  display: inline-block;
  padding: 8px 12px;
  background: #d4edda;
  color: #155724;
  font-size: 16px;
  font-weight: 500;
  border-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .profile-container {
    padding: 10px;
  }

  .profile-header {
    padding: 30px 20px;
  }

  .profile-body {
    padding: 24px 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>

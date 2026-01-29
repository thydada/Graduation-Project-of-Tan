<template>
  <div class="employee-layout">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">GPOT 快递</h2>
        <p class="sidebar-subtitle">员工工作台</p>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.name"
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-text">{{ item.title }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">
          <span>退出登录</span>
        </button>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="content-header">
        <h1 class="content-title">欢迎使用 GPOT 快递管理系统</h1>
        <div class="user-info">
          <span class="user-name">{{ userName }}</span>
          <span class="user-dept">部门：{{ department }}</span>
        </div>
      </div>

      <div class="content-body">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { ref, onMounted, computed } from 'vue'

export default {
  name: 'EmployeeLayout',
  setup() {
    const router = useRouter()
    const userName = ref('')
    const department = ref('')

    // 根据部门显示不同的菜单项
    const menuItems = computed(() => {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const dept = user.department || ''
      
      const baseItems = [
        {
          name: 'EmployeeProfile',
          title: '个人信息管理',
          path: '/employee/profile',
          icon: '人'
        }
      ]

      // 根据部门添加不同的快递管理菜单
      if (dept === 'A') {
        baseItems.push({
          name: 'EmployeePackage',
          title: '快递取得情况',
          path: '/employee/package',
          icon: '件'
        })
        baseItems.push({
          name: 'EmployeeDelivery',
          title: '快递运送',
          path: '/employee/delivery',
          icon: '送'
        })
      } else if (dept === 'B') {
        baseItems.push({
          name: 'EmployeePackage',
          title: '快递入库',
          path: '/employee/package',
          icon: '件'
        })
        baseItems.push({
          name: 'EmployeeOutbound',
          title: '快递出库',
          path: '/employee/outbound',
          icon: '出'
        })
      }

      // 添加异常件查询
      baseItems.push({
        name: 'EmployeeException',
        title: '异常件查询',
        path: '/employee/exception',
        icon: '警'
      })

      return baseItems
    })

    const handleLogout = () => {
      // 清除用户会话
      localStorage.removeItem('user')
      router.push('/')
    }

    onMounted(() => {
      // 从localStorage获取用户信息
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      userName.value = user.realName || user.username || '员工'
      department.value = user.department || '未知'
    })

    return {
      menuItems,
      userName,
      department,
      handleLogout
    }
  }
}
</script>

<style scoped>
.employee-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 左侧导航栏 */
.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #B22222 0%, #8B0000 100%);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.1);
}

.sidebar-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  letter-spacing: 1px;
}

.sidebar-subtitle {
  margin: 8px 0 0 0;
  font-size: 12px;
  text-align: center;
  opacity: 0.9;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: all 0.2s ease;
  border-left: 4px solid transparent;
  border-radius: 0;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border-left-color: rgba(255, 255, 255, 0.6);
}

.nav-item.active {
  background-color: #B22222;
  color: white;
  border-left-color: white;
  font-weight: 600;
}

.nav-icon {
  margin-right: 12px;
  font-size: 18px;
}

.nav-text {
  font-size: 16px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 0;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn:hover {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-header {
  background: #f8f8f8;
  padding: 24px 32px;
  border-bottom: 2px solid #DC143C;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.content-title {
  margin: 0;
  color: #333333;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.user-name {
  color: #555555;
  font-size: 16px;
  font-weight: 500;
}

.user-dept {
  color: #888888;
  font-size: 14px;
}

.content-body {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  background: #ffffff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }

  .content-header {
    padding: 18px 20px;
  }

  .content-title {
    font-size: 22px;
  }

  .content-body {
    padding: 20px;
  }
}
</style>

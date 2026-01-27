<template>
  <div class="employee-layout">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">员工管理系统</h2>
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
        <div class="user-badge">
          <span class="user-icon">👤</span>
          <span class="user-dept">{{ department }}</span>
        </div>
        <button @click="handleLogout" class="logout-btn">
          <span class="nav-icon">退出</span>
          <span>登录</span>
        </button>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="content-header">
        <h1 class="content-title">{{ currentPageTitle }}</h1>
        <div class="user-info">
          <span class="user-name">{{ userName }}</span>
        </div>
      </div>

      <div class="content-body">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'EmployeeLayout',
  setup() {
    const router = useRouter()
    const route = useRoute()

    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
    const userName = computed(() => userInfo.realName || userInfo.username || '员工')
    const department = computed(() => {
      const dept = userInfo.department || ''
      const deptMap = {
        'A': '部门 A（取件审核）',
        'B': '部门 B（核验入库）'
      }
      return deptMap[dept] || '员工'
    })

    // 菜单项
    const menuItems = [
      {
        name: 'EmployeeProfile',
        title: '个人信息管理',
        path: '/employee/profile',
        icon: '个'
      },
      {
        name: 'PageA',
        title: '快递取件情况审核',
        path: '/employee/pickup',
        icon: '审'
      },
      {
        name: 'Exception',
        title: '异常件查询',
        path: '/employee/exception',
        icon: '警'
      }
    ]

    // 计算当前页面标题
    const currentPageTitle = computed(() => {
      const currentRoute = menuItems.find(item => item.path === route.path)
      return currentRoute ? currentRoute.title : '员工管理系统'
    })

    const handleLogout = () => {
      localStorage.removeItem('user')
      router.push('/')
    }

    return {
      menuItems,
      userName,
      department,
      currentPageTitle,
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

/* 左侧导航栏 - 红色主题（员工） */
.sidebar {
  width: 260px;
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
  font-size: 18px;
  font-weight: 700;
  text-align: center;
  letter-spacing: 1px;
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
  font-weight: 600;
}

.nav-text {
  font-size: 15px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-badge {
  display: flex;
  align-items: center;
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
  margin-bottom: 12px;
}

.user-icon {
  margin-right: 8px;
  font-size: 16px;
}

.user-dept {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
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
  align-items: center;
}

.user-name {
  color: #555555;
  font-size: 16px;
  font-weight: 500;
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

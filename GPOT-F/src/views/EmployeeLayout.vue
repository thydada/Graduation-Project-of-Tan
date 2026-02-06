<template>
  <div class="employee-layout">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">GPOT 快递</h2>
        <p class="sidebar-subtitle">员工工作台</p>
      </div>

      <nav class="sidebar-nav">
        <div v-for="item in menuItems" :key="item.name">
          <!-- 有子菜单的父级（快递入库） -->
          <div
            v-if="item.children"
            class="nav-item nav-group"
            @click="toggleMenu(item.name)"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-text nav-group-title">
              {{ item.title }}
              <span class="nav-group-arrow">
                {{ expandedMenus.includes(item.name) ? '▲' : '▼' }}
              </span>
            </span>
          </div>

          <div v-if="item.children && expandedMenus.includes(item.name)" class="sub-menu">
            <router-link
              v-for="child in item.children"
              :key="child.name"
              :to="child.path"
              class="nav-item sub-menu-link"
              active-class="active"
            >
              <span class="nav-text">{{ child.title }}</span>
            </router-link>
          </div>

          <!-- 普通单层菜单 -->
          <router-link
            v-else
            :to="item.path"
            class="nav-item"
            active-class="active"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-text">{{ item.title }}</span>
          </router-link>
        </div>
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
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted, computed, watch } from 'vue'

export default {
  name: 'EmployeeLayout',
  setup() {
    const router = useRouter()
    const userName = ref('')
    const department = ref('')

    // 员工不再区分部门A/B：统一使用同一套工作台菜单
    // “快递入库”作为父菜单，下面有“扫码入库”和“手动入库”两个子项
    const menuItems = computed(() => {
      return [
        {
          name: 'EmployeeProfile',
          title: '个人信息管理',
          path: '/employee/profile',
          icon: '人'
        },
        {
          name: 'EmployeePackageGroup',
          title: '快递入库',
          icon: '件',
          children: [
            {
              name: 'EmployeePackageScan',
              title: '扫码入库',
              path: '/employee/package/scan'
            },
            {
              name: 'EmployeePackageManual',
              title: '手动入库',
              path: '/employee/package/manual'
            },
            {
              name: 'EmployeePackageException',
              title: '异常件登记',
              path: '/employee/package/exception'
            }
          ]
        },
        {
          name: 'EmployeeException',
          title: '异常件查询',
          path: '/employee/exception',
          icon: '警'
        },
        {
          name: 'EmployeeAllPackages',
          title: '全部包裹',
          path: '/employee/all-packages',
          icon: '表'
        }
      ]
    })

    const expandedMenus = ref([])

    const toggleMenu = (name) => {
      if (expandedMenus.value.includes(name)) {
        expandedMenus.value = expandedMenus.value.filter(n => n !== name)
      } else {
        expandedMenus.value.push(name)
      }
    }

    // 监听路由变化，自动展开快递入库父菜单
    const unwatch = router.afterEach((to) => {
      if (to.name === 'EmployeePackageScan' || to.name === 'EmployeePackageManual' || to.name === 'EmployeePackageException') {
        if (!expandedMenus.value.includes('EmployeePackageGroup')) {
          expandedMenus.value.push('EmployeePackageGroup')
        }
      }
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
      // 员工不再区分A/B
      department.value = 'B'

      // 如果当前在快递入库的子页面，自动展开父菜单
      const currentRoute = router.currentRoute.value.name
      if (currentRoute === 'EmployeePackageScan' || currentRoute === 'EmployeePackageManual' || currentRoute === 'EmployeePackageException') {
        expandedMenus.value = ['EmployeePackageGroup']
      }
    })

    return {
      menuItems,
      userName,
      department,
      handleLogout,
      expandedMenus,
      toggleMenu
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

.nav-group {
  cursor: pointer;
}

.nav-group-title {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-group-arrow {
  font-size: 12px;
  margin-left: 8px;
}

.sub-menu {
  background: rgba(0, 0, 0, 0.08);
}

.sub-menu-link {
  padding-left: 48px;
  font-size: 14px;
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

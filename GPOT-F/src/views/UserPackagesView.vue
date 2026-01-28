<template>
  <div class="user-packages-container">
    <div class="page-header">
      <h1>我的包裹</h1>
      <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-number">{{ tempPackages.length }}</span>
        <span class="stat-label">待审核</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ formalPackages.length }}</span>
        <span class="stat-label">已入库</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ exceptionPackages.length }}</span>
        <span class="stat-label">异常包裹</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ allPackages.length }}</span>
        <span class="stat-label">全部包裹</span>
      </div>
    </div>

    <!-- 包裹列表 -->
    <div class="packages-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span class="tab-count">({{ getTabCount(tab.key) }})</span>
      </button>
    </div>

    <!-- 包裹表格 -->
    <div class="table-container">
      <table class="packages-table">
        <thead>
          <tr>
            <th>快递单号</th>
            <th>收件人</th>
            <th>收件人电话</th>
            <th>收件地址</th>
            <th>包裹类型</th>
            <th>重量(kg)</th>
            <th>状态</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in displayedPackages" :key="pkg.id">
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td>{{ pkg.receiverName }}</td>
            <td>{{ pkg.receiverPhone }}</td>
            <td class="address">{{ pkg.receiverAddress }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(pkg)]">
                {{ getStatusText(pkg) }}
              </span>
            </td>
            <td>{{ formatDate(pkg.createTime || pkg.reportTime) }}</td>
          </tr>
          <tr v-if="displayedPackages.length === 0 && !loading">
            <td colspan="8" class="empty-message">
              {{ getEmptyMessage() }}
            </td>
          </tr>
        </tbody>
      </table>
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
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'UserPackagesView',
  setup() {
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const activeTab = ref('all')
    
    const tempPackages = ref([])
    const formalPackages = ref([])
    const exceptionPackages = ref([])

    const tabs = [
      { key: 'all', label: '全部包裹' },
      { key: 'temp', label: '待审核' },
      { key: 'formal', label: '已入库' },
      { key: 'exception', label: '异常包裹' }
    ]

    // 所有包裹
    const allPackages = computed(() => {
      const all = []
      
      // 临时包裹 - 直接使用数据库中的status字段
      tempPackages.value.forEach(pkg => {
        all.push({
          ...pkg,
          source: 'temp'
        })
      })
      
      // 正式包裹 - 直接使用数据库中的status字段
      formalPackages.value.forEach(pkg => {
        all.push({
          ...pkg,
          source: 'formal'
        })
      })
      
      // 异常包裹 - 直接使用数据库中的异常类型作为状态
      exceptionPackages.value.forEach(pkg => {
        all.push({
          id: pkg.id,
          trackingNumber: pkg.trackingNumber,
          receiverName: '-',
          receiverPhone: '-',
          receiverAddress: '-',
          packageType: '-',
          weight: '-',
          source: 'exception',
          status: `异常：${pkg.exceptionType || '未知'}`,
          exceptionType: pkg.exceptionType,
          exceptionReason: pkg.exceptionReason,
          reportTime: pkg.reportTime
        })
      })
      
      // 按时间倒序排列
      return all.sort((a, b) => {
        const timeA = a.createTime || a.reportTime
        const timeB = b.createTime || b.reportTime
        return new Date(timeB) - new Date(timeA)
      })
    })

    // 显示的包裹列表
    const displayedPackages = computed(() => {
      if (activeTab.value === 'all') {
        return allPackages.value
      } else if (activeTab.value === 'temp') {
        // 临时包裹 - 直接使用数据库中的status字段
        return tempPackages.value.map(pkg => ({
          ...pkg,
          source: 'temp'
        }))
      } else if (activeTab.value === 'formal') {
        // 正式包裹 - 直接使用数据库中的status字段
        return formalPackages.value.map(pkg => ({
          ...pkg,
          source: 'formal'
        }))
      } else if (activeTab.value === 'exception') {
        // 异常包裹 - 直接使用数据库中的异常类型作为状态
        return exceptionPackages.value.map(pkg => ({
          id: pkg.id,
          trackingNumber: pkg.trackingNumber,
          receiverName: '-',
          receiverPhone: '-',
          receiverAddress: '-',
          packageType: '-',
          weight: '-',
          source: 'exception',
          status: `异常：${pkg.exceptionType || '未知'}`,
          exceptionType: pkg.exceptionType,
          exceptionReason: pkg.exceptionReason,
          reportTime: pkg.reportTime
        }))
      }
      return []
    })

    // 获取标签页数量
    const getTabCount = (key) => {
      if (key === 'all') return allPackages.value.length
      if (key === 'temp') return tempPackages.value.length
      if (key === 'formal') return formalPackages.value.length
      if (key === 'exception') return exceptionPackages.value.length
      return 0
    }

    // 获取状态文本 - 直接使用数据库中的status字段
    const getStatusText = (pkg) => {
      return pkg.status || '未知'
    }

    // 获取状态样式类 - 根据数据库中的status值判断
    const getStatusClass = (pkg) => {
      const status = pkg.status || ''
      if (status.includes('异常')) {
        return 'status-error'
      }
      if (status === '已入库' || status === '已取件' || status === '审核完成') {
        return 'status-success'
      }
      if (status === '待取件' || status === '待核验' || status === '待入库' || status === '运输中') {
        return 'status-pending'
      }
      return 'status-pending'
    }

    // 获取空数据提示
    const getEmptyMessage = () => {
      if (activeTab.value === 'all') return '暂无包裹记录'
      if (activeTab.value === 'temp') return '暂无待审核的包裹'
      if (activeTab.value === 'formal') return '暂无已入库的包裹'
      if (activeTab.value === 'exception') return '暂无异常包裹'
      return '暂无数据'
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    // 获取用户包裹
    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user || !user.id) {
          errorMessage.value = '用户未登录'
          return
        }

        const response = await api.getUserAllPackages(user.id)
        if (response.data.success) {
          tempPackages.value = response.data.data.tempPackages || []
          formalPackages.value = response.data.data.formalPackages || []
          exceptionPackages.value = response.data.data.exceptionPackages || []
          successMessage.value = '数据加载成功'
          setTimeout(() => {
            successMessage.value = ''
          }, 2000)
        } else {
          errorMessage.value = response.data.message || '获取包裹列表失败'
        }
      } catch (error) {
        console.error('获取包裹列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取包裹列表失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    // 页面加载时获取数据
    onMounted(() => {
      fetchPackages()
    })

    return {
      loading,
      successMessage,
      errorMessage,
      activeTab,
      tabs,
      tempPackages,
      formalPackages,
      exceptionPackages,
      allPackages,
      displayedPackages,
      getTabCount,
      getStatusText,
      getStatusClass,
      getEmptyMessage,
      formatDate,
      fetchPackages
    }
  }
}
</script>

<style scoped>
.user-packages-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.refresh-btn {
  padding: 12px 24px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.refresh-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.refresh-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 统计卡片 */
.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 20px 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #DC143C;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 标签页 */
.packages-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  background: white;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  background: #f5f5f5;
  color: #666;
  border: 2px solid transparent;
  border-radius: 0;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tab-btn:hover {
  background: #e8e8e8;
}

.tab-btn.active {
  background: #DC143C;
  color: white;
  border-color: #DC143C;
  font-weight: 600;
}

.tab-count {
  font-size: 14px;
  opacity: 0.8;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.packages-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.packages-table th {
  background: #DC143C;
  color: white;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap;
}

.packages-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee;
  color: #333;
}

.packages-table tr:hover {
  background: #fff5f5;
}

.packages-table tr:last-child td {
  border-bottom: none;
}

.tracking-number {
  font-family: monospace;
  font-weight: 600;
  color: #DC143C;
}

.address {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-success {
  background: #d4edda;
  color: #155724;
}

.status-error {
  background: #f8d7da;
  color: #721c24;
}

.empty-message {
  text-align: center;
  padding: 48px !important;
  color: #999;
  font-size: 16px;
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

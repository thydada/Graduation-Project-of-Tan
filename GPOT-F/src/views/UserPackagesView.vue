<template>
  <div class="user-packages-container">
    <div class="page-header">
      <h1>我的包裹</h1>
      <button class="refresh-btn" @click="handleRefresh" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-number">{{ totalFormalCount }}</span>
        <span class="stat-label">已入库</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ totalExceptionCount }}</span>
        <span class="stat-label">异常包裹</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ totalCount }}</span>
        <span class="stat-label">全部包裹</span>
      </div>
    </div>

    <!-- 查询和标签页 -->
    <div class="search-tabs-container">
      <!-- 查询框 -->
      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="请输入快递单号、收件人姓名或电话进行查询"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch" :disabled="loading">
          查询
        </button>
        <button 
          v-if="searchKeyword" 
          class="clear-btn" 
          @click="handleClearSearch"
        >
          清除
        </button>
      </div>

      <!-- 包裹列表标签页 -->
      <div class="packages-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="['tab-btn', { active: activeTab === tab.key }]"
          @click="handleTabChange(tab.key)"
        >
          {{ tab.label }}
          <span class="tab-count">({{ getTabCount(tab.key) }})</span>
        </button>
      </div>
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
          <tr v-if="loading">
            <td colspan="8" class="loading-message">
              加载中...
            </td>
          </tr>
          <tr v-for="pkg in displayedPackages" :key="`${pkg.source}-${pkg.id}`">
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

    <!-- 分页组件 -->
    <div v-if="totalPages > 1" class="pagination-container">
      <div class="pagination-info">
        共 {{ totalCount }} 条记录，第 {{ currentPage + 1 }} / {{ totalPages }} 页
      </div>
      <div class="pagination-controls">
        <button
          class="page-btn"
          :disabled="currentPage === 0 || loading"
          @click="handlePageChange(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-numbers">
          <button
            v-for="page in visiblePages"
            :key="page"
            :class="['page-number-btn', { active: page === currentPage + 1 }]"
            :disabled="loading"
            @click="handlePageChange(page - 1)"
          >
            {{ page }}
          </button>
        </span>
        <button
          class="page-btn"
          :disabled="currentPage >= totalPages - 1 || loading"
          @click="handlePageChange(currentPage + 1)"
        >
          下一页
        </button>
        <select
          v-model.number="pageSize"
          class="page-size-select"
          :disabled="loading"
          @change="handlePageSizeChange"
        >
          <option :value="10">10 条/页</option>
          <option :value="20">20 条/页</option>
          <option :value="50">50 条/页</option>
        </select>
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
import { ref, computed, onMounted, watch } from 'vue'
import api from '../services/api'

export default {
  name: 'UserPackagesView',
  setup() {
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const activeTab = ref('all')
    const searchKeyword = ref('')
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalCount = ref(0)
    const totalPages = ref(0)
    const totalFormalCount = ref(0)
    const totalExceptionCount = ref(0)
    
    const formalPackages = ref([])
    const exceptionPackages = ref([])

    const tabs = [
      { key: 'all', label: '全部包裹' },
      { key: 'formal', label: '已入库' },
      { key: 'exception', label: '异常包裹' }
    ]

    // 所有包裹
    const allPackages = computed(() => {
      const all = []
      
      // 正式包裹
      formalPackages.value.forEach(pkg => {
        all.push({
          ...pkg,
          source: 'formal'
        })
      })
      
      // 异常包裹
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
      } else if (activeTab.value === 'formal') {
        return formalPackages.value.map(pkg => ({
          ...pkg,
          source: 'formal'
        }))
      } else if (activeTab.value === 'exception') {
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

    // 计算可见的页码
    const visiblePages = computed(() => {
      const pages = []
      const maxVisible = 5
      let start = Math.max(0, currentPage.value - Math.floor(maxVisible / 2))
      let end = Math.min(totalPages.value, start + maxVisible)
      
      if (end - start < maxVisible) {
        start = Math.max(0, end - maxVisible)
      }
      
      for (let i = start; i < end; i++) {
        pages.push(i + 1)
      }
      return pages
    })

    // 获取标签页数量
    const getTabCount = (key) => {
      if (key === 'all') return totalCount.value
      if (key === 'formal') return totalFormalCount.value
      if (key === 'exception') return totalExceptionCount.value
      return 0
    }

    // 获取状态文本
    const getStatusText = (pkg) => {
      return pkg.status || '未知'
    }

    // 获取状态样式类
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
      if (searchKeyword.value) {
        return '未找到匹配的包裹记录'
      }
      if (activeTab.value === 'all') return '暂无包裹记录'
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

    // 获取用户包裹（分页）
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

        const response = await api.getUserAllPackagesWithPagination(
          user.id,
          searchKeyword.value,
          currentPage.value,
          pageSize.value,
          activeTab.value
        )
        
        if (response.data.success) {
          const data = response.data.data
          formalPackages.value = data.formalPackages || []
          exceptionPackages.value = data.exceptionPackages || []
          totalCount.value = data.totalElements || 0
          totalPages.value = data.totalPages || 0
          currentPage.value = data.currentPage || 0
          
          // 获取总数统计（需要单独查询）
          await fetchTotalCounts(user.id)
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

    // 获取总数统计
    const fetchTotalCounts = async (userId) => {
      try {
        // 分别获取正式包裹和异常包裹的总数（不应用查询条件）
        const formalResponse = await api.getUserAllPackagesWithPagination(userId, '', 0, 1, 'formal')
        const exceptionResponse = await api.getUserAllPackagesWithPagination(userId, '', 0, 1, 'exception')
        
        if (formalResponse.data.success) {
          totalFormalCount.value = formalResponse.data.data.totalElements || 0
        }
        if (exceptionResponse.data.success) {
          totalExceptionCount.value = exceptionResponse.data.data.totalElements || 0
        }
      } catch (error) {
        console.error('获取统计信息失败:', error)
      }
    }

    // 处理标签页切换
    const handleTabChange = (tabKey) => {
      activeTab.value = tabKey
      currentPage.value = 0
      fetchPackages()
    }

    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 0
      fetchPackages()
    }

    // 清除搜索
    const handleClearSearch = () => {
      searchKeyword.value = ''
      currentPage.value = 0
      fetchPackages()
    }

    // 处理刷新
    const handleRefresh = () => {
      fetchPackages()
    }

    // 处理页码变化
    const handlePageChange = (page) => {
      currentPage.value = page
      fetchPackages()
      // 滚动到顶部
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    // 处理每页大小变化
    const handlePageSizeChange = () => {
      currentPage.value = 0
      fetchPackages()
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
      searchKeyword,
      formalPackages,
      exceptionPackages,
      allPackages,
      displayedPackages,
      currentPage,
      pageSize,
      totalCount,
      totalPages,
      totalFormalCount,
      totalExceptionCount,
      visiblePages,
      getTabCount,
      getStatusText,
      getStatusClass,
      getEmptyMessage,
      formatDate,
      handleTabChange,
      handleSearch,
      handleClearSearch,
      handleRefresh,
      handlePageChange,
      handlePageSizeChange
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

/* 查询和标签页容器 */
.search-tabs-container {
  margin-bottom: 24px;
}

/* 查询框 */
.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #ddd;
  border-radius: 0;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #DC143C;
}

.search-btn,
.clear-btn {
  padding: 12px 24px;
  border: 2px solid #DC143C;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.search-btn {
  background: #DC143C;
  color: white;
}

.search-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.search-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.clear-btn {
  background: white;
  color: #DC143C;
}

.clear-btn:hover {
  background: #fff5f5;
}

/* 标签页 */
.packages-tabs {
  display: flex;
  gap: 12px;
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

.loading-message,
.empty-message {
  text-align: center;
  padding: 48px !important;
  color: #999;
  font-size: 16px;
}

/* 分页样式 */
.pagination-container {
  margin-top: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  padding: 8px 16px;
  background: white;
  color: #DC143C;
  border: 2px solid #DC143C;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.page-btn:hover:not(:disabled) {
  background: #DC143C;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 8px;
  background: white;
  color: #666;
  border: 2px solid #ddd;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.page-number-btn:hover:not(:disabled) {
  border-color: #DC143C;
  color: #DC143C;
}

.page-number-btn.active {
  background: #DC143C;
  color: white;
  border-color: #DC143C;
}

.page-number-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-size-select {
  padding: 8px 12px;
  border: 2px solid #ddd;
  border-radius: 0;
  font-size: 14px;
  cursor: pointer;
  outline: none;
  transition: border-color 0.2s;
}

.page-size-select:focus {
  border-color: #DC143C;
}

.page-size-select:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

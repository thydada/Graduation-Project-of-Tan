<template>
  <div class="track-package-view">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>寄件包裹跟踪</h2>
      <p class="subtitle">查看您的所有寄件包裹状态和物流信息</p>
    </div>

    <!-- 统计信息卡片 -->
    <div class="stats-container">
      <div class="stat-card pending">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <span class="stat-value">{{ tempPackages.length }}</span>
          <span class="stat-label">待处理包裹</span>
        </div>
      </div>
      <div class="stat-card completed">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <span class="stat-value">{{ formalPackages.length }}</span>
          <span class="stat-label">已完成包裹</span>
        </div>
      </div>
      <div class="stat-card exception">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <span class="stat-value">{{ exceptionPackages.length }}</span>
          <span class="stat-label">异常件</span>
        </div>
      </div>
      <div class="stat-card total">
        <div class="stat-icon">📬</div>
        <div class="stat-info">
          <span class="stat-value">{{ totalCount }}</span>
          <span class="stat-label">总包裹数</span>
        </div>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="search-section">
      <input
        v-model="searchTrackingNumber"
        type="text"
        class="search-input"
        placeholder="请输入快递单号搜索"
      >
      <button @click="handleSearch" class="search-btn">搜索</button>
    </div>

    <!-- 标签页切换 -->
    <div class="tabs-container">
      <div class="tab-buttons">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'all' }"
          @click="activeTab = 'all'"
        >全部包裹</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'pending' }"
          @click="activeTab = 'pending'"
        >待处理</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'completed' }"
          @click="activeTab = 'completed'"
        >已完成</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'exception' }"
          @click="activeTab = 'exception'"
        >异常件</button>
      </div>

      <!-- 全部包裹 -->
      <div v-if="activeTab === 'all'" class="tab-content">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-else-if="allPackages.length === 0" class="empty-container">
          <div class="empty-icon">📭</div>
          <p class="empty-text">暂无寄件包裹记录</p>
          <button class="btn-primary" @click="$router.push('/main/send-package')">去寄件</button>
        </div>
        <div v-else class="package-cards">
          <div
            v-for="pkg in allPackages"
            :key="pkg.trackingNumber"
            class="package-card"
            :class="pkg.source"
            @click="showPackageDetail(pkg)"
          >
            <div class="card-header">
              <span class="tracking-number">{{ pkg.trackingNumber }}</span>
              <span class="status-tag" :class="getStatusClass(pkg)">{{ getStatusText(pkg) }}</span>
            </div>
            <div class="card-body">
              <div class="route-info">
                <div class="route-item">
                  <span class="route-label">寄件人</span>
                  <span class="route-value">{{ pkg.senderName }}</span>
                </div>
                <div class="route-arrow">→</div>
                <div class="route-item">
                  <span class="route-label">收件人</span>
                  <span class="route-value">{{ pkg.receiverName }}</span>
                </div>
              </div>
              <div class="package-info">
                <span class="info-item">📦 {{ pkg.packageType }}</span>
                <span class="info-item">⚖️ {{ pkg.weight }}kg</span>
                <span class="info-item">📏 {{ pkg.size }}</span>
              </div>
              <div class="time-info">
                <span class="time-label">创建时间</span>
                <span class="time-value">{{ formatTime(pkg.createTime || pkg.reportTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <span class="source-tag" :class="pkg.source">
                {{ getSourceText(pkg.source) }}
              </span>
              <span class="detail-hint">点击查看详情 →</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 待处理包裹 -->
      <div v-if="activeTab === 'pending'" class="tab-content">
        <div v-if="tempPackages.length === 0" class="empty-container">
          <div class="empty-icon">✅</div>
          <p class="empty-text">暂无待处理的包裹</p>
        </div>
        <div v-else class="package-cards">
          <div
            v-for="pkg in tempPackages"
            :key="pkg.trackingNumber"
            class="package-card temp"
            @click="showPackageDetail(pkg)"
          >
            <div class="card-header">
              <span class="tracking-number">{{ pkg.trackingNumber }}</span>
              <span class="status-tag pending">{{ pkg.status }}</span>
            </div>
            <div class="card-body">
              <div class="route-info">
                <div class="route-item">
                  <span class="route-label">寄件人</span>
                  <span class="route-value">{{ pkg.senderName }}</span>
                </div>
                <div class="route-arrow">→</div>
                <div class="route-item">
                  <span class="route-label">收件人</span>
                  <span class="route-value">{{ pkg.receiverName }}</span>
                </div>
              </div>
              <div class="package-info">
                <span class="info-item">📦 {{ pkg.packageType }}</span>
                <span class="info-item">⚖️ {{ pkg.weight }}kg</span>
              </div>
              <div class="progress-section">
                <div class="progress-step" :class="{ active: true, completed: pkg.pickupSuccess === 1 }">
                  <span class="step-icon">📝</span>
                  <span class="step-text">已提交</span>
                </div>
                <div class="progress-line" :class="{ active: pkg.pickupSuccess === 1 }"></div>
                <div class="progress-step" :class="{ active: pkg.pickupSuccess === 1, completed: pkg.verificationSuccess === 1 }">
                  <span class="step-icon">🏪</span>
                  <span class="step-text">取件{{ pkg.pickupSuccess === 1 ? '完成' : '中' }}</span>
                </div>
                <div class="progress-line" :class="{ active: pkg.verificationSuccess === 1 }"></div>
                <div class="progress-step" :class="{ active: pkg.verificationSuccess === 1 }">
                  <span class="step-icon">✓</span>
                  <span class="step-text">核验{{ pkg.verificationSuccess === 1 ? '完成' : '中' }}</span>
                </div>
              </div>
            </div>
            <div class="card-footer">
              <span class="time-value">创建于 {{ formatTime(pkg.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 已完成包裹 -->
      <div v-if="activeTab === 'completed'" class="tab-content">
        <div v-if="formalPackages.length === 0" class="empty-container">
          <div class="empty-icon">📦</div>
          <p class="empty-text">暂无已完成的包裹</p>
        </div>
        <div v-else class="package-cards">
          <div
            v-for="pkg in formalPackages"
            :key="pkg.trackingNumber"
            class="package-card formal"
            @click="showPackageDetail(pkg)"
          >
            <div class="card-header">
              <span class="tracking-number">{{ pkg.trackingNumber }}</span>
              <span class="status-tag completed">{{ pkg.status }}</span>
            </div>
            <div class="card-body">
              <div class="route-info">
                <div class="route-item">
                  <span class="route-label">寄件人</span>
                  <span class="route-value">{{ pkg.senderName }}</span>
                </div>
                <div class="route-arrow">→</div>
                <div class="route-item">
                  <span class="route-label">收件人</span>
                  <span class="route-value">{{ pkg.receiverName }}</span>
                </div>
              </div>
              <div class="package-info">
                <span class="info-item">📦 {{ pkg.packageType }}</span>
                <span class="info-item">⚖️ {{ pkg.weight }}kg</span>
                <span class="info-item">📏 {{ pkg.size }}</span>
              </div>
              <div class="time-info">
                <span class="time-label">入库时间</span>
                <span class="time-value">{{ formatTime(pkg.entryTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <span class="warehouse-info" v-if="pkg.warehouseId">
                🏭 仓库{{ pkg.warehouseId }} / 货架{{ pkg.shelfId }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 异常件 -->
      <div v-if="activeTab === 'exception'" class="tab-content">
        <div v-if="exceptionPackages.length === 0" class="empty-container">
          <div class="empty-icon">✅</div>
          <p class="empty-text">暂无异常件记录</p>
        </div>
        <div v-else class="package-cards">
          <div
            v-for="pkg in exceptionPackages"
            :key="pkg.trackingNumber"
            class="package-card exception"
            @click="showExceptionDetail(pkg)"
          >
            <div class="card-header">
              <span class="tracking-number">{{ pkg.trackingNumber }}</span>
              <span class="status-tag" :class="getExceptionStatusClass(pkg.handleStatus)">{{ pkg.handleStatus }}</span>
            </div>
            <div class="card-body">
              <div class="exception-type">
                <span class="exception-icon">⚠️</span>
                <span class="exception-type-text">{{ pkg.exceptionType }}</span>
              </div>
              <div class="exception-reason" v-if="pkg.exceptionReason">
                <span class="reason-label">异常原因：</span>
                <span class="reason-text">{{ pkg.exceptionReason }}</span>
              </div>
              <div class="exception-source">
                <span class="source-label">异常来源：</span>
                <span class="source-text">{{ pkg.source === 'pickup' ? '取件异常' : '核验异常' }}</span>
              </div>
              <div class="exception-info">
                <span class="info-item">👤 报告人：{{ pkg.reportEmployeeName || '未知' }}</span>
                <span class="info-item">📅 报告时间：{{ formatTime(pkg.reportTime) }}</span>
              </div>
              <div class="handle-info" v-if="pkg.handleStatus === '已处理'">
                <span class="info-item">✅ 处理结果：{{ pkg.handleResult }}</span>
                <span class="info-item">📅 处理时间：{{ formatTime(pkg.handleTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <span class="detail-hint">点击查看详情 →</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 包裹详情对话框 -->
    <div v-if="detailDialogVisible" class="dialog-overlay" @click.self="detailDialogVisible = false">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>包裹详情 - {{ currentPackage?.trackingNumber }}</h3>
          <button class="close-btn" @click="detailDialogVisible = false">×</button>
        </div>
        <div class="dialog-body" v-if="currentPackage">
          <div class="detail-section">
            <h4>📍 基本信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">快递单号</span>
                <span class="tracking-number-highlight">{{ currentPackage.trackingNumber }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">状态</span>
                <span class="status-tag" :class="getStatusClass(currentPackage)">{{ getStatusText(currentPackage) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">包裹类型</span>
                <span>{{ currentPackage.packageType }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">重量</span>
                <span>{{ currentPackage.weight }}kg</span>
              </div>
              <div class="info-row">
                <span class="info-label">尺寸</span>
                <span>{{ currentPackage.size }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">来源</span>
                <span>{{ getSourceText(currentPackage.source) }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>👤 寄件人信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">姓名</span>
                <span>{{ currentPackage.senderName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">电话</span>
                <span>{{ currentPackage.senderPhone }}</span>
              </div>
              <div class="info-row full-width">
                <span class="info-label">地址</span>
                <span>{{ currentPackage.senderAddress }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>📬 收件人信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">姓名</span>
                <span>{{ currentPackage.receiverName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">电话</span>
                <span>{{ currentPackage.receiverPhone }}</span>
              </div>
              <div class="info-row full-width">
                <span class="info-label">地址</span>
                <span>{{ currentPackage.receiverAddress }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>📅 时间信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">创建时间</span>
                <span>{{ formatTime(currentPackage.createTime) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">入库时间</span>
                <span>{{ formatTime(currentPackage.entryTime) || '暂无' }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section" v-if="currentPackage.warehouseId">
            <h4>🏭 存储信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">仓库ID</span>
                <span>{{ currentPackage.warehouseId }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">货架ID</span>
                <span>{{ currentPackage.shelfId }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 异常件详情对话框 -->
    <div v-if="exceptionDialogVisible" class="dialog-overlay" @click.self="exceptionDialogVisible = false">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>异常件详情 - {{ currentException?.trackingNumber }}</h3>
          <button class="close-btn" @click="exceptionDialogVisible = false">×</button>
        </div>
        <div class="dialog-body" v-if="currentException">
          <div class="detail-section">
            <h4>⚠️ 异常信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">快递单号</span>
                <span class="tracking-number-highlight">{{ currentException.trackingNumber }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">异常状态</span>
                <span class="status-tag" :class="getExceptionStatusClass(currentException.handleStatus)">{{ currentException.handleStatus }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">异常类型</span>
                <span>{{ currentException.exceptionType }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">异常来源</span>
                <span>{{ currentException.source === 'pickup' ? '取件异常' : '核验异常' }}</span>
              </div>
              <div class="info-row full-width">
                <span class="info-label">异常原因</span>
                <span>{{ currentException.exceptionReason || '未填写原因' }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>👤 报告信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">报告员工</span>
                <span>{{ currentException.reportEmployeeName || '未知' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">报告时间</span>
                <span>{{ formatTime(currentException.reportTime) }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section" v-if="currentException.handleStatus === '已处理'">
            <h4>✅ 处理信息</h4>
            <div class="info-grid">
              <div class="info-row">
                <span class="info-label">处理员工</span>
                <span>{{ currentException.handleEmployeeId ? '员工' + currentException.handleEmployeeId : '未知' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">处理时间</span>
                <span>{{ formatTime(currentException.handleTime) }}</span>
              </div>
              <div class="info-row full-width">
                <span class="info-label">处理结果</span>
                <span>{{ currentException.handleResult }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiService from '../services/api'

export default {
  name: 'TrackPackageView',
  setup() {
    const router = useRouter()
    const loading = ref(true)
    const activeTab = ref('all')
    const searchTrackingNumber = ref('')
    const tempPackages = ref([])
    const formalPackages = ref([])
    const exceptionPackages = ref([])
    const detailDialogVisible = ref(false)
    const exceptionDialogVisible = ref(false)
    const currentPackage = ref(null)
    const currentException = ref(null)
    const searchResult = ref(null)

    // 计算属性
    const allPackages = computed(() => {
      const temp = tempPackages.value.map(pkg => ({
        ...pkg,
        source: 'temp'
      }))
      const formal = formalPackages.value.map(pkg => ({
        ...pkg,
        source: 'formal'
      }))
      const exceptions = exceptionPackages.value.map(pkg => ({
        ...pkg,
        source: 'exception',
        senderName: '用户寄件',
        receiverName: '收件人',
        packageType: '未知',
        weight: '未知',
        size: '未知'
      }))
      return [...temp, ...formal, ...exceptions].sort((a, b) => {
        const timeA = a.createTime || a.reportTime
        const timeB = b.createTime || b.reportTime
        return new Date(timeB) - new Date(timeA)
      })
    })

    const totalCount = computed(() => {
      return tempPackages.value.length + formalPackages.value.length
    })

    // 获取用户所有包裹
    const fetchUserPackages = async () => {
      try {
        loading.value = true
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user || !user.id) {
          alert('用户未登录')
          return
        }

        const response = await apiService.getAllUserPackages(user.id)
        if (response.data && response.data.code === 200) {
          const data = response.data.data
          tempPackages.value = data.tempPackages || []
          formalPackages.value = data.formalPackages || []
          exceptionPackages.value = data.exceptionPackages || []
        }
      } catch (error) {
        console.error('获取包裹列表失败:', error)
        alert('获取包裹列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索包裹
    const handleSearch = async () => {
      if (!searchTrackingNumber.value.trim()) {
        searchResult.value = null
        return
      }

      try {
        loading.value = true
        const response = await apiService.getPackageByTrackingNumber(searchTrackingNumber.value)
        if (response.data && response.data.code === 200) {
          searchResult.value = response.data.data
          alert('找到包裹')
        } else {
          searchResult.value = null
          alert('未找到该快递单号')
        }
      } catch (error) {
        console.error('搜索失败:', error)
        alert('搜索失败')
      } finally {
        loading.value = false
      }
    }

    // 显示包裹详情
    const showPackageDetail = (pkg) => {
      currentPackage.value = pkg
      detailDialogVisible.value = true
    }

    // 显示异常件详情
    const showExceptionDetail = (pkg) => {
      currentException.value = pkg
      exceptionDialogVisible.value = true
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return '暂无'
      return new Date(time).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 获取状态样式类
    const getStatusClass = (pkg) => {
      if (pkg.source === 'exception') {
        return 'exception'
      }
      if (pkg.source === 'temp') {
        return 'pending'
      }
      const statusMap = {
        '待入库': 'pending',
        '已入库': 'completed',
        '已取件': 'info',
        '核验成功': 'completed',
        '核验失败': 'error'
      }
      return statusMap[pkg.status] || 'info'
    }

    // 获取状态文本
    const getStatusText = (pkg) => {
      if (pkg.source === 'exception') {
        return '异常件'
      }
      return pkg.status || '未知'
    }

    // 获取来源文本
    const getSourceText = (source) => {
      const sourceMap = {
        'temp': '待处理',
        'formal': '已完成',
        'exception': '异常件'
      }
      return sourceMap[source] || '未知'
    }

    // 获取异常件状态样式类
    const getExceptionStatusClass = (status) => {
      const statusMap = {
        '待处理': 'pending',
        '已处理': 'completed',
        '处理中': 'info'
      }
      return statusMap[status] || 'info'
    }

    onMounted(() => {
      fetchUserPackages()
    })

    return {
      loading,
      activeTab,
      searchTrackingNumber,
      tempPackages,
      formalPackages,
      exceptionPackages,
      allPackages,
      totalCount,
      detailDialogVisible,
      exceptionDialogVisible,
      currentPackage,
      currentException,
      searchResult,
      fetchUserPackages,
      handleSearch,
      showPackageDetail,
      showExceptionDetail,
      formatTime,
      getStatusClass,
      getStatusText,
      getSourceText,
      getExceptionStatusClass,
      router
    }
  }
}
</script>

<style scoped>
.track-package-view {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 统计信息卡片 */
.stats-container {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stat-card.pending {
  border-left: 4px solid #e6a23c;
}

.stat-card.completed {
  border-left: 4px solid #67c23a;
}

.stat-card.exception {
  border-left: 4px solid #f56c6c;
}

.stat-card.total {
  border-left: 4px solid #409eff;
}

.stat-icon {
  font-size: 36px;
  margin-right: 16px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 搜索框 */
.search-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.search-input {
  width: 400px;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #409eff;
}

.search-btn {
  padding: 12px 24px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #66b1ff;
}

/* 标签页样式 */
.tabs-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.tab-buttons {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-btn {
  padding: 12px 24px;
  background: none;
  border: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.tab-btn:hover {
  color: #409eff;
}

.tab-btn.active {
  color: #409eff;
  font-weight: 600;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #409eff;
}

.tab-content {
  min-height: 200px;
}

/* 包裹列表 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  margin-bottom: 20px;
}

.btn-primary {
  padding: 12px 24px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #66b1ff;
}

/* 包裹卡片 */
.package-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.package-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.package-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.package-card.temp {
  border-left: 4px solid #e6a23c;
}

.package-card.formal {
  border-left: 4px solid #67c23a;
}

.package-card.exception {
  border-left: 4px solid #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.tracking-number {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  font-family: 'Monaco', 'Consolas', monospace;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.pending {
  background: #fdf6ec;
  color: #e6a23c;
}

.status-tag.completed {
  background: #f0f9eb;
  color: #67c23a;
}

.status-tag.info {
  background: #ecf5ff;
  color: #409eff;
}

.status-tag.error {
  background: #fef0f0;
  color: #f56c6c;
}

.status-tag.exception {
  background: #fef0f0;
  color: #f56c6c;
}

.card-body {
  margin-bottom: 16px;
}

.route-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.route-item {
  flex: 1;
  text-align: center;
}

.route-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.route-value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.route-arrow {
  font-size: 20px;
  color: #409eff;
  margin: 0 12px;
}

.package-info {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.info-item {
  font-size: 13px;
  color: #666;
}

.time-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.time-label {
  color: #999;
}

.time-value {
  color: #666;
}

.progress-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.progress-step.active {
  opacity: 1;
}

.progress-step.completed .step-icon {
  background: #67c23a;
  color: white;
}

.step-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f0f0f0;
  font-size: 14px;
  margin-bottom: 4px;
}

.step-text {
  font-size: 12px;
  color: #666;
}

.progress-line {
  flex: 1;
  height: 2px;
  background: #e0e0e0;
  margin: 0 4px;
}

.progress-line.active {
  background: #67c23a;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.source-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.source-tag.temp {
  background: #fdf6ec;
  color: #e6a23c;
}

.source-tag.formal {
  background: #f0f9eb;
  color: #67c23a;
}

.source-tag.exception {
  background: #fef0f0;
  color: #f56c6c;
}

.detail-hint {
  font-size: 13px;
  color: #409eff;
}

.warehouse-info {
  font-size: 13px;
  color: #666;
}

/* 异常件样式 */
.exception-type {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding: 12px;
  background: #fef0f0;
  border-radius: 8px;
}

.exception-icon {
  font-size: 20px;
}

.exception-type-text {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.exception-reason {
  margin-bottom: 12px;
  font-size: 14px;
}

.reason-label {
  color: #999;
}

.reason-text {
  color: #333;
}

.exception-source {
  margin-bottom: 12px;
  font-size: 14px;
}

.source-label {
  color: #999;
}

.source-text {
  color: #666;
}

.exception-info,
.handle-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.exception-info .info-item,
.handle-info .info-item {
  font-size: 13px;
  color: #666;
}

/* 对话框样式 */
.dialog-overlay {
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

.dialog-content {
  background: white;
  border-radius: 12px;
  width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f0f0f0;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.close-btn:hover {
  background: #e0e0e0;
}

.dialog-body {
  padding: 24px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.info-row.full-width {
  grid-column: span 2;
}

.info-label {
  width: 80px;
  font-size: 13px;
  color: #999;
  flex-shrink: 0;
}

.tracking-number-highlight {
  font-family: 'Monaco', 'Consolas', monospace;
  font-weight: 600;
  color: #409eff;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-container {
    flex-wrap: wrap;
  }

  .stat-card {
    flex: 1 1 45%;
  }

  .search-section {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .package-cards {
    grid-template-columns: 1fr;
  }

  .route-info {
    flex-direction: column;
    gap: 12px;
  }

  .route-arrow {
    transform: rotate(90deg);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-row.full-width {
    grid-column: span 1;
    flex-direction: column;
    align-items: flex-start;
  }

  .info-label {
    margin-bottom: 4px;
  }

  .dialog-content {
    width: 90%;
    margin: 20px;
  }
}
</style>

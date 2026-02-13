<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">GPOT 运维监控大屏</h1>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="header-actions">
        <button class="export-btn" @click="exportData" :disabled="exporting">
          {{ exporting ? '导出中...' : '导出数据' }}
        </button>
        <button class="refresh-btn" @click="fetchStatistics" :disabled="loading">
          {{ loading ? '刷新中...' : '刷新数据' }}
        </button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="dashboard-content">
      <!-- 标签页导航 -->
      <div class="tabs-container">
        <div class="tabs-header">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            :class="['tab-button', { active: activeTab === tab.id }]"
            @click="activeTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 总览标签页 -->
      <div v-show="activeTab === 'overview'" class="tab-content">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-header">
              <span class="stat-label">总包裹数</span>
            </div>
            <div class="stat-value">{{ statistics.totalPackages || 0 }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-header">
              <span class="stat-label">待入库</span>
            </div>
            <div class="stat-value pending">{{ statistics.pendingInbound || 0 }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-header">
              <span class="stat-label">已入库</span>
            </div>
            <div class="stat-value success">{{ statistics.inStock || 0 }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-header">
              <span class="stat-label">已取件</span>
            </div>
            <div class="stat-value delivered">{{ statistics.delivered || 0 }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-header">
              <span class="stat-label">异常件</span>
            </div>
            <div class="stat-value exception">{{ statistics.exception || 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 包裹统计标签页 -->
      <div v-show="activeTab === 'packages'" class="tab-content">
        <div class="charts-section">
          <div class="chart-row">
            <div class="chart-card">
              <div class="chart-header">
                <h3>包裹状态分布</h3>
              </div>
              <div class="chart-content">
                <div class="status-list">
                  <div class="status-item">
                    <span class="status-dot pending-dot"></span>
                    <span class="status-label">待入库</span>
                    <span class="status-value">{{ statistics.pendingInbound || 0 }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot in-stock-dot"></span>
                    <span class="status-label">已入库</span>
                    <span class="status-value">{{ statistics.inStock || 0 }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot transit-dot"></span>
                    <span class="status-label">运输中</span>
                    <span class="status-value">{{ statistics.inTransit || 0 }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot delivered-dot"></span>
                    <span class="status-label">已取件</span>
                    <span class="status-value">{{ statistics.delivered || 0 }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot exception-dot"></span>
                    <span class="status-label">异常</span>
                    <span class="status-value">{{ statistics.exception || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="chart-card">
              <div class="chart-header">
                <h3>异常件处理状态</h3>
              </div>
              <div class="chart-content">
                <div class="exception-stats">
                  <div class="exception-item">
                    <div class="exception-value">{{ statistics.pendingException || 0 }}</div>
                    <div class="exception-label">待处理</div>
                  </div>
                  <div class="exception-item">
                    <div class="exception-value">{{ statistics.processingException || 0 }}</div>
                    <div class="exception-label">处理中</div>
                  </div>
                  <div class="exception-item">
                    <div class="exception-value">{{ statistics.completedException || 0 }}</div>
                    <div class="exception-label">已处理</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户统计标签页 -->
      <div v-show="activeTab === 'users'" class="tab-content">
        <div class="charts-section">
          <div class="chart-row">
            <div class="chart-card full-width">
              <div class="chart-header">
                <h3>系统用户统计</h3>
              </div>
              <div class="chart-content">
                <div class="user-stats">
                  <div class="user-item">
                    <div class="user-value">{{ statistics.totalUsers || 0 }}</div>
                    <div class="user-label">普通用户</div>
                  </div>
                  <div class="user-item">
                    <div class="user-value">{{ statistics.totalEmployees || 0 }}</div>
                    <div class="user-label">员工</div>
                  </div>
                  <div class="user-item">
                    <div class="user-value">{{ statistics.totalAdmins || 0 }}</div>
                    <div class="user-label">管理员</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作统计标签页 -->
      <div v-show="activeTab === 'operations'" class="tab-content">
        <div class="charts-section">
          <div class="chart-row">
            <div class="chart-card">
              <div class="chart-header">
                <h3>操作统计</h3>
              </div>
              <div class="chart-content">
                <div class="operation-stats">
                  <div class="operation-item">
                    <div class="operation-value">{{ statistics.totalEntries || 0 }}</div>
                    <div class="operation-label">入库记录</div>
                  </div>
                  <div class="operation-item">
                    <div class="operation-value">{{ statistics.totalOutbounds || 0 }}</div>
                    <div class="operation-label">出库记录</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="chart-card line-chart-card">
              <div class="chart-header">
                <h3>近7日入库趋势</h3>
              </div>
              <div class="chart-content">
                <div class="line-chart-wrapper">
                  <svg class="line-chart" :width="chartWidth" :height="chartHeight">
                    <defs>
                      <linearGradient id="lineGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" style="stop-color:#DC143C;stop-opacity:0.6" />
                        <stop offset="100%" style="stop-color:#DC143C;stop-opacity:0.1" />
                      </linearGradient>
                    </defs>
                    <g v-if="chartPoints.length > 0">
                      <polyline
                        :points="linePoints"
                        fill="none"
                        stroke="#DC143C"
                        stroke-width="3"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <polygon
                        :points="areaPoints"
                        fill="url(#lineGradient)"
                      />
                      <g v-for="(point, index) in chartPoints" :key="index">
                        <circle
                          :cx="point.x"
                          :cy="point.y"
                          r="6"
                          fill="#DC143C"
                          stroke="#fff"
                          stroke-width="2"
                        />
                        <text
                          :x="point.x"
                          :y="point.y - 12"
                          text-anchor="middle"
                          fill="#DC143C"
                          font-size="13"
                          font-weight="700"
                        >{{ point.value }}</text>
                      </g>
                      <g v-for="(item, index) in dailyEntryData" :key="index">
                        <text
                          :x="chartPoints[index]?.x || 0"
                          :y="chartHeight - 10"
                          text-anchor="middle"
                          fill="#666"
                          font-size="12"
                          font-weight="500"
                        >{{ item.dateLabel }}</text>
                      </g>
                    </g>
                    <g v-else>
                      <text
                        :x="chartWidth / 2"
                        :y="chartHeight / 2"
                        text-anchor="middle"
                        fill="#999"
                        font-size="14"
                      >暂无数据</text>
                    </g>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- AI分析弹窗 -->
    <div v-if="showAiDialog" class="ai-dialog-overlay" @click="closeAiDialog">
      <div class="ai-dialog" @click.stop>
        <div class="ai-dialog-header">
          <h3>AI数据分析</h3>
          <button class="close-btn" @click="closeAiDialog">×</button>
        </div>
        <div class="ai-dialog-content">
          <div v-if="aiLoading" class="ai-loading">
            <div class="loading-spinner"></div>
            <p>AI正在分析数据...</p>
          </div>
          <div v-else class="ai-content">
            <pre>{{ aiAnalysis }}</pre>
          </div>
        </div>
        <div class="ai-dialog-footer">
          <button class="confirm-btn" @click="closeAiDialog">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

export default {
  name: 'AdminDashboardView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const exporting = ref(false)
    const statistics = ref({})
    const dailyEntryData = ref([])
    const currentTime = ref('')
    const activeTab = ref('overview')
    const chartWidth = 800
    const chartHeight = 300
    const padding = 50
    const showAiDialog = ref(false)
    const aiAnalysis = ref('')
    const aiLoading = ref(false)
    let timeInterval = null
    let dataInterval = null

    const tabs = [
      { id: 'overview', label: '总览' },
      { id: 'packages', label: '包裹统计' },
      { id: 'users', label: '用户统计' },
      { id: 'operations', label: '操作统计' }
    ]

    const updateTime = () => {
      const now = new Date()
      currentTime.value = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    }

    const fetchStatistics = async () => {
      loading.value = true
      try {
        const response = await api.getAdminStatistics()
        if (response.data && response.data.success) {
          statistics.value = response.data.data
        } else {
          console.error('获取统计数据失败:', response.data?.message)
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      } finally {
        loading.value = false
      }
    }

    const fetchDailyEntryStatistics = async () => {
      try {
        const response = await api.getDailyEntryStatistics(7)
        if (response.data && response.data.success) {
          dailyEntryData.value = response.data.data
        } else {
          console.error('获取每日入库统计失败:', response.data?.message)
        }
      } catch (error) {
        console.error('获取每日入库统计失败:', error)
      }
    }

    const maxValue = computed(() => {
      if (dailyEntryData.value.length === 0) return 10
      const max = Math.max(...dailyEntryData.value.map(item => item.count))
      return max > 0 ? Math.ceil(max * 1.2) : 10
    })

    const chartPoints = computed(() => {
      if (dailyEntryData.value.length === 0) return []
      const width = chartWidth - padding * 2
      const height = chartHeight - padding * 2
      const stepX = width / (dailyEntryData.value.length - 1 || 1)
      
      return dailyEntryData.value.map((item, index) => {
        const x = padding + index * stepX
        const y = padding + height - (item.count / maxValue.value) * height
        return { x, y, value: item.count }
      })
    })

    const linePoints = computed(() => {
      return chartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
    })

    const areaPoints = computed(() => {
      if (chartPoints.value.length === 0) return ''
      const bottomY = chartHeight - padding
      const firstX = chartPoints.value[0].x
      const lastX = chartPoints.value[chartPoints.value.length - 1].x
      const points = chartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
      return `${firstX},${bottomY} ${points} ${lastX},${bottomY}`
    })

    const exportData = async () => {
      exporting.value = true
      try {
        // 确保数据是最新的
        await fetchStatistics()
        await fetchDailyEntryStatistics()

        // 生成文本内容
        const now = new Date()
        const exportTime = now.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        })

        let content = '='.repeat(60) + '\n'
        content += 'GPOT 运维监控数据报告\n'
        content += '='.repeat(60) + '\n'
        content += `导出时间: ${exportTime}\n`
        content += '\n'

        // 总览数据
        content += '【总览数据】\n'
        content += '-'.repeat(60) + '\n'
        content += `总包裹数: ${statistics.value.totalPackages || 0}\n`
        content += `待入库: ${statistics.value.pendingInbound || 0}\n`
        content += `已入库: ${statistics.value.inStock || 0}\n`
        content += `已取件: ${statistics.value.delivered || 0}\n`
        content += `异常件: ${statistics.value.exception || 0}\n`
        content += '\n'

        // 包裹状态分布
        content += '【包裹状态分布】\n'
        content += '-'.repeat(60) + '\n'
        content += `待入库: ${statistics.value.pendingInbound || 0}\n`
        content += `已入库: ${statistics.value.inStock || 0}\n`
        content += `运输中: ${statistics.value.inTransit || 0}\n`
        content += `已取件: ${statistics.value.delivered || 0}\n`
        content += `异常: ${statistics.value.exception || 0}\n`
        content += '\n'

        // 异常件处理状态
        content += '【异常件处理状态】\n'
        content += '-'.repeat(60) + '\n'
        content += `待处理: ${statistics.value.pendingException || 0}\n`
        content += `处理中: ${statistics.value.processingException || 0}\n`
        content += `已处理: ${statistics.value.completedException || 0}\n`
        content += '\n'

        // 系统用户统计
        content += '【系统用户统计】\n'
        content += '-'.repeat(60) + '\n'
        content += `普通用户: ${statistics.value.totalUsers || 0}\n`
        content += `员工: ${statistics.value.totalEmployees || 0}\n`
        content += `管理员: ${statistics.value.totalAdmins || 0}\n`
        content += '\n'

        // 操作统计
        content += '【操作统计】\n'
        content += '-'.repeat(60) + '\n'
        content += `入库记录: ${statistics.value.totalEntries || 0}\n`
        content += `出库记录: ${statistics.value.totalOutbounds || 0}\n`
        content += '\n'

        // 近7日入库趋势
        content += '【近7日入库趋势】\n'
        content += '-'.repeat(60) + '\n'
        if (dailyEntryData.value.length > 0) {
          dailyEntryData.value.forEach(item => {
            content += `${item.dateLabel}: ${item.count} 件\n`
          })
        } else {
          content += '暂无数据\n'
        }
        content += '\n'

        content += '='.repeat(60) + '\n'
        content += '报告结束\n'
        content += '='.repeat(60) + '\n'

        // 调用后端API保存到根目录
        const response = await api.exportAdminData(content)
        if (response.data && response.data.success) {
          const filePath = response.data.data.filePath
          const fileName = response.data.data.fileName
          
          // 如果有AI分析结果，显示弹窗
          if (response.data.data.aiAnalysis) {
            aiAnalysis.value = response.data.data.aiAnalysis
            showAiDialog.value = true
            aiLoading.value = false
          }
          
          alert(`数据已成功保存到根目录！\n文件名: ${fileName}\n路径: ${filePath}`)
        } else {
          throw new Error(response.data?.message || '导出失败')
        }
      } catch (error) {
        console.error('导出数据失败:', error)
        alert('导出数据失败：' + (error.response?.data?.message || error.message || '请稍后重试'))
      } finally {
        exporting.value = false
      }
    }

    const closeAiDialog = () => {
      showAiDialog.value = false
      aiAnalysis.value = ''
    }

    const handleLogout = () => {
      localStorage.removeItem('user')
      router.push('/')
    }

    onMounted(() => {
      updateTime()
      fetchStatistics()
      fetchDailyEntryStatistics()
      timeInterval = setInterval(updateTime, 1000)
      dataInterval = setInterval(() => {
        fetchStatistics()
        fetchDailyEntryStatistics()
      }, 30000)
    })

    onUnmounted(() => {
      if (timeInterval) clearInterval(timeInterval)
      if (dataInterval) clearInterval(dataInterval)
    })

    return {
      loading,
      exporting,
      statistics,
      dailyEntryData,
      currentTime,
      activeTab,
      tabs,
      chartWidth,
      chartHeight,
      chartPoints,
      linePoints,
      areaPoints,
      showAiDialog,
      aiAnalysis,
      aiLoading,
      fetchStatistics,
      exportData,
      closeAiDialog,
      handleLogout
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 24px;
}

.dashboard-header {
  background: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  padding: 24px 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dashboard-title {
  margin: 0;
  color: #333;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.current-time {
  font-size: 16px;
  color: #DC143C;
  font-weight: 600;
  font-family: monospace;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.export-btn,
.refresh-btn,
.logout-btn {
  padding: 10px 20px;
  border: 2px solid #DC143C;
  background: white;
  color: #DC143C;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 0;
  transition: all 0.2s ease;
}

.export-btn:hover:not(:disabled),
.refresh-btn:hover:not(:disabled),
.logout-btn:hover {
  background: #DC143C;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.export-btn:disabled,
.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 标签页样式 */
.tabs-container {
  background: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  padding: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.tabs-header {
  display: flex;
  border-bottom: 2px solid #DC143C;
  background: #fff;
}

.tab-button {
  flex: 1;
  padding: 16px 24px;
  border: none;
  background: white;
  color: #666;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 0;
  transition: all 0.2s ease;
  border-right: 1px solid #e0e0e0;
  position: relative;
}

.tab-button:last-child {
  border-right: none;
}

.tab-button:hover {
  background: #fff5f5;
  color: #DC143C;
}

.tab-button.active {
  background: #DC143C;
  color: white;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #DC143C;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  padding: 20px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 20, 60, 0.2);
}

.stat-header {
  margin-bottom: 12px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #DC143C;
  line-height: 1.2;
}

.stat-value.pending {
  color: #ffc107;
}

.stat-value.success {
  color: #28a745;
}

.stat-value.transit {
  color: #17a2b8;
}

.stat-value.delivered {
  color: #6f42c1;
}

.stat-value.exception {
  color: #dc3545;
}

.charts-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.chart-card {
  background: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  padding: 24px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 12px rgba(220, 20, 60, 0.15);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.line-chart-card {
  grid-column: 1 / -1;
}

.chart-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #DC143C;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.chart-content {
  min-height: 150px;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f8f8;
  border-left: 4px solid;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.pending-dot {
  background: #ffc107;
  border-color: #ffc107;
}

.in-stock-dot {
  background: #28a745;
  border-color: #28a745;
}

.transit-dot {
  background: #17a2b8;
  border-color: #17a2b8;
}

.delivered-dot {
  background: #6f42c1;
  border-color: #6f42c1;
}

.exception-dot {
  background: #dc3545;
  border-color: #dc3545;
}

.status-label {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.status-value {
  font-size: 16px;
  font-weight: 700;
  color: #DC143C;
}

.exception-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 20px;
}

.exception-item {
  text-align: center;
  flex: 1;
}

.exception-value {
  font-size: 36px;
  font-weight: 700;
  color: #DC143C;
  margin-bottom: 8px;
}

.exception-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.user-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-item {
  padding: 16px;
  background: #f8f8f8;
  border-left: 4px solid #DC143C;
}

.user-value {
  font-size: 28px;
  font-weight: 700;
  color: #DC143C;
  margin-bottom: 6px;
}

.user-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.operation-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.operation-item {
  padding: 16px;
  background: #f8f8f8;
  border-left: 4px solid #DC143C;
}

.operation-value {
  font-size: 28px;
  font-weight: 700;
  color: #DC143C;
  margin-bottom: 6px;
}

.operation-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.line-chart-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  background: #fafafa;
  border: 1px solid #e0e0e0;
}

.line-chart {
  display: block;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .export-btn,
  .refresh-btn,
  .logout-btn {
    flex: 1;
    min-width: 120px;
  }

  .tabs-header {
    flex-wrap: wrap;
  }

  .tab-button {
    min-width: 50%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}

/* AI分析弹窗样式 */
.ai-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.ai-dialog {
  background: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.ai-dialog-header {
  padding: 20px 24px;
  border-bottom: 2px solid #DC143C;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #DC143C;
  color: white;
}

.ai-dialog-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 28px;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  transition: transform 0.2s;
}

.close-btn:hover {
  transform: scale(1.2);
}

.ai-dialog-content {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #DC143C;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.ai-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.ai-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.ai-dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: flex-end;
}

.confirm-btn {
  padding: 10px 24px;
  border: 2px solid #DC143C;
  background: #DC143C;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 0;
  transition: all 0.2s ease;
}

.confirm-btn:hover {
  background: #b01030;
  border-color: #b01030;
}
</style>

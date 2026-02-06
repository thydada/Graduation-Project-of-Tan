<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">GPOT 运维监控大屏</h1>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="header-actions">
        <button class="refresh-btn" @click="fetchStatistics" :disabled="loading">
          {{ loading ? '刷新中...' : '刷新数据' }}
        </button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="dashboard-content">
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

          <div class="chart-card">
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
                <div class="operation-item">
                  <div class="operation-value">{{ statistics.totalTempPackages || 0 }}</div>
                  <div class="operation-label">临时包裹</div>
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
    const statistics = ref({})
    const dailyEntryData = ref([])
    const currentTime = ref('')
    const chartWidth = 800
    const chartHeight = 300
    const padding = 50
    let timeInterval = null
    let dataInterval = null

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
      statistics,
      dailyEntryData,
      currentTime,
      chartWidth,
      chartHeight,
      chartPoints,
      linePoints,
      areaPoints,
      fetchStatistics,
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

.refresh-btn:hover:not(:disabled),
.logout-btn:hover {
  background: #DC143C;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
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

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>

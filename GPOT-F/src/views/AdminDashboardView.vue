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

      <!-- 操作统计标签页 - 运维数据大屏 -->
      <div v-show="activeTab === 'operations'" class="tab-content">
        <div class="operation-dashboard">
          <!-- 顶部统计数据 -->
          <div class="top-stats-row">
            <div class="mini-stat-card">
              <div class="mini-stat-icon">A</div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ statistics.totalEntries || 0 }}</div>
                <div class="mini-stat-label">总入库</div>
              </div>
            </div>
            <div class="mini-stat-card">
              <div class="mini-stat-icon">B</div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ statistics.totalOutbounds || 0 }}</div>
                <div class="mini-stat-label">总出库</div>
              </div>
            </div>
            <div class="mini-stat-card">
              <div class="mini-stat-icon">C</div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ statistics.exception || 0 }}</div>
                <div class="mini-stat-label">异常件</div>
              </div>
            </div>
            <div class="mini-stat-card">
              <div class="mini-stat-icon">D</div>
              <div class="mini-stat-info">
                <div class="mini-stat-value">{{ statistics.totalPackages || 0 }}</div>
                <div class="mini-stat-label">包裹总量</div>
              </div>
            </div>
          </div>

          <!-- 四格图表区域 -->
          <div class="charts-grid">
            <!-- 图表1: 近7日入库趋势 -->
            <div class="grid-chart-card">
              <div class="chart-header">
                <h3>近7日入库趋势</h3>
                <span class="chart-subtitle">每日入库包裹数量</span>
              </div>
              <div class="chart-body">
                <div class="line-chart-mini-wrapper">
                  <svg class="line-chart-mini" :width="miniChartWidth" :height="miniChartHeight">
                    <defs>
                      <linearGradient id="lineGradientMini" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" style="stop-color:#DC143C;stop-opacity:0.4" />
                        <stop offset="100%" style="stop-color:#DC143C;stop-opacity:0.05" />
                      </linearGradient>
                    </defs>
                    <g v-if="dailyEntryData.length > 0">
                      <polyline
                        :points="miniLinePoints"
                        fill="none"
                        stroke="#DC143C"
                        stroke-width="2.5"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <polygon
                        :points="miniAreaPoints"
                        fill="url(#lineGradientMini)"
                      />
                      <g v-for="(point, index) in miniChartPoints" :key="index">
                        <circle
                          :cx="point.x"
                          :cy="point.y"
                          r="4"
                          fill="#DC143C"
                          stroke="#fff"
                          stroke-width="1.5"
                        />
                        <text
                          :x="point.x"
                          :y="point.y - 8"
                          text-anchor="middle"
                          fill="#DC143C"
                          font-size="10"
                          font-weight="600"
                        >{{ point.value }}</text>
                      </g>
                      <g v-for="(item, index) in dailyEntryData" :key="'label-' + index">
                        <text
                          :x="miniChartPoints[index]?.x || 0"
                          :y="miniChartHeight - 5"
                          text-anchor="middle"
                          fill="#666"
                          font-size="9"
                        >{{ item.dateLabel }}</text>
                      </g>
                    </g>
                    <g v-else>
                      <text
                        :x="miniChartWidth / 2"
                        :y="miniChartHeight / 2"
                        text-anchor="middle"
                        fill="#999"
                        font-size="12"
                      >暂无数据</text>
                    </g>
                  </svg>
                </div>
              </div>
            </div>

            <!-- 图表2: 包裹状态分布 -->
            <div class="grid-chart-card">
              <div class="chart-header">
                <h3>包裹状态分布</h3>
                <span class="chart-subtitle">各类状态包裹占比</span>
              </div>
              <div class="chart-body">
                <div class="bar-chart-mini">
                  <div class="bar-item" v-for="(item, index) in statusDistribution" :key="index">
                    <div class="bar-label">{{ item.label }}</div>
                    <div class="bar-track">
                      <div
                        class="bar-fill"
                        :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                      ></div>
                    </div>
                    <div class="bar-value">{{ item.value }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 图表3: 近7日出库趋势 -->
            <div class="grid-chart-card">
              <div class="chart-header">
                <h3>近7日出库趋势</h3>
                <span class="chart-subtitle">每日出库包裹数量</span>
              </div>
              <div class="chart-body">
                <div class="bar-chart-mini">
                  <div class="vertical-bar-item" v-for="(item, index) in outboundTrendData" :key="index">
                    <div class="v-bar-wrapper">
                      <div
                        class="v-bar-fill"
                        :style="{ height: item.percentage + '%' }"
                      ></div>
                    </div>
                    <div class="v-bar-value">{{ item.value }}</div>
                    <div class="v-bar-label">{{ item.dateLabel }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 图表4: 异常件处理统计 -->
            <div class="grid-chart-card">
              <div class="chart-header">
                <h3>异常件处理统计</h3>
                <span class="chart-subtitle">处理进度分布</span>
              </div>
              <div class="chart-body">
                <div class="exception-dashboard">
                  <div class="exception-progress-ring">
                    <svg :width="ringSize" :height="ringSize">
                      <circle
                        class="ring-bg"
                        :cx="ringSize/2"
                        :cy="ringSize/2"
                        :r="ringRadius"
                        fill="none"
                        stroke="#eee"
                        stroke-width="8"
                      />
                      <circle
                        class="ring-progress"
                        :cx="ringSize/2"
                        :cy="ringSize/2"
                        :r="ringRadius"
                        fill="none"
                        stroke="#DC143C"
                        stroke-width="8"
                        :stroke-dasharray="ringDashArray"
                        stroke-linecap="round"
                        :transform="`rotate(-90 ${ringSize/2} ${ringSize/2})`"
                      />
                      <text
                        :x="ringSize/2"
                        :y="ringSize/2 - 5"
                        text-anchor="middle"
                        fill="#DC143C"
                        font-size="18"
                        font-weight="700"
                      >{{ exceptionCompleteRate }}%</text>
                      <text
                        :x="ringSize/2"
                        :y="ringSize/2 + 12"
                        text-anchor="middle"
                        fill="#666"
                        font-size="10"
                      >完成率</text>
                    </svg>
                  </div>
                  <div class="exception-legend">
                    <div class="legend-item">
                      <span class="legend-dot pending"></span>
                      <span class="legend-text">待处理: {{ statistics.pendingException || 0 }}</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-dot processing"></span>
                      <span class="legend-text">处理中: {{ statistics.processingException || 0 }}</span>
                    </div>
                    <div class="legend-item">
                      <span class="legend-dot completed"></span>
                      <span class="legend-text">已完成: {{ statistics.completedException || 0 }}</span>
                    </div>
                  </div>
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
    const dailyOutboundData = ref([])
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
      { id: 'operations', label: '运维数据大屏' }
    ]

    // 小图表尺寸配置
    const miniChartWidth = 260
    const miniChartHeight = 160
    const miniPadding = 30

    // 环形图配置
    const ringSize = 120
    const ringRadius = 45

    // 计算状态分布数据
    const statusDistribution = computed(() => {
      const total = statistics.value.totalPackages || 1
      const items = [
        { label: '待入库', value: statistics.value.pendingInbound || 0, color: '#ffc107' },
        { label: '已入库', value: statistics.value.inStock || 0, color: '#28a745' },
        { label: '已取件', value: statistics.value.delivered || 0, color: '#6f42c1' },
        { label: '异常件', value: statistics.value.exception || 0, color: '#dc3545' }
      ]
      return items.map(item => ({
        ...item,
        percentage: Math.round((item.value / total) * 100)
      }))
    })

    // 计算异常件完成率
    const exceptionCompleteRate = computed(() => {
      const total = (statistics.value.pendingException || 0) +
                     (statistics.value.processingException || 0) +
                     (statistics.value.completedException || 0)
      if (total === 0) return 0
      const completed = statistics.value.completedException || 0
      return Math.round((completed / total) * 100)
    })

    // 环形图进度
    const ringDashArray = computed(() => {
      const circumference = 2 * Math.PI * ringRadius
      const progress = (exceptionCompleteRate.value / 100) * circumference
      return `${progress} ${circumference}`
    })

    // 近7日出库趋势（使用真实API数据）
    const outboundTrendData = computed(() => {
      // 使用真实API获取的出库数据
      if (dailyOutboundData.value.length > 0) {
        const maxOutbound = Math.max(...dailyOutboundData.value.map(item => item.count), 1)
        return dailyOutboundData.value.map(item => ({
          dateLabel: item.dateLabel,
          value: item.count,
          percentage: maxOutbound > 0 ? Math.round((item.count / maxOutbound) * 100) : 0
        }))
      }
      // 如果没有数据，显示空状态
      return Array(7).fill(0).map((_, i) => ({
        dateLabel: '',
        value: 0,
        percentage: 0
      }))
    })

    // 小图表计算
    const miniMaxValue = computed(() => {
      if (dailyEntryData.value.length === 0) return 10
      const max = Math.max(...dailyEntryData.value.map(item => item.count))
      return max > 0 ? Math.ceil(max * 1.2) : 10
    })

    const miniChartPoints = computed(() => {
      if (dailyEntryData.value.length === 0) return []
      const width = miniChartWidth - miniPadding * 2
      const height = miniChartHeight - miniPadding * 2
      const stepX = width / (dailyEntryData.value.length - 1 || 1)

      return dailyEntryData.value.map((item, index) => {
        const x = miniPadding + index * stepX
        const y = miniPadding + height - (item.count / miniMaxValue.value) * height
        return { x, y, value: item.count }
      })
    })

    const miniLinePoints = computed(() => {
      return miniChartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
    })

    const miniAreaPoints = computed(() => {
      if (miniChartPoints.value.length === 0) return ''
      const bottomY = miniChartHeight - miniPadding
      const firstX = miniChartPoints.value[0].x
      const lastX = miniChartPoints.value[miniChartPoints.value.length - 1].x
      const points = miniChartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
      return `${firstX},${bottomY} ${points} ${lastX},${bottomY}`
    })

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

    const fetchDailyOutboundStatistics = async () => {
      try {
        const response = await api.getDailyOutboundStatistics(3)
        if (response.data && response.data.success) {
          dailyOutboundData.value = response.data.data
        } else {
          console.error('获取每日出库统计失败:', response.data?.message)
        }
      } catch (error) {
        console.error('获取每日出库统计失败:', error)
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
        await fetchDailyOutboundStatistics()

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
      fetchDailyOutboundStatistics()
      timeInterval = setInterval(updateTime, 1000)
      dataInterval = setInterval(() => {
        fetchStatistics()
        fetchDailyEntryStatistics()
        fetchDailyOutboundStatistics()
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
      dailyOutboundData,
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
      handleLogout,
      // 运维数据大屏新增数据
      miniChartWidth,
      miniChartHeight,
      miniChartPoints,
      miniLinePoints,
      miniAreaPoints,
      statusDistribution,
      ringSize,
      ringRadius,
      ringDashArray,
      exceptionCompleteRate,
      outboundTrendData
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

/* 运维数据大屏样式 */
.operation-dashboard {
  background: #0a1628;
  border: 2px solid #DC143C;
  padding: 20px;
  border-radius: 8px;
}

.top-stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.mini-stat-card {
  background: linear-gradient(135deg, #1a2942 0%, #0d1929 100%);
  border: 1px solid #2a4a6a;
  border-radius: 6px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.mini-stat-icon {
  font-size: 24px;
  font-weight: 700;
  color: #DC143C;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(220, 20, 60, 0.2);
  border-radius: 8px;
}

.mini-stat-info {
  flex: 1;
}

.mini-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #DC143C;
  line-height: 1.2;
}

.mini-stat-label {
  font-size: 12px;
  color: #8a9ab0;
  margin-top: 4px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.grid-chart-card {
  background: linear-gradient(135deg, #1a2942 0%, #0d1929 100%);
  border: 1px solid #2a4a6a;
  border-radius: 6px;
  overflow: hidden;
}

.grid-chart-card .chart-header {
  padding: 12px 16px;
  border-bottom: 1px solid #2a4a6a;
  background: rgba(220, 20, 60, 0.1);
}

.grid-chart-card .chart-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.chart-subtitle {
  font-size: 11px;
  color: #6a7a8a;
}

.grid-chart-card .chart-body {
  padding: 12px;
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 小型折线图 */
.line-chart-mini-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

.line-chart-mini {
  display: block;
}

/* 横向条形图 */
.bar-chart-mini {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bar-label {
  width: 50px;
  font-size: 11px;
  color: #8a9ab0;
  text-align: right;
  flex-shrink: 0;
}

.bar-track {
  flex: 1;
  height: 16px;
  background: #1a2942;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.bar-value {
  width: 40px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
}

/* 垂直柱状图 */
.vertical-bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.v-bar-wrapper {
  width: 100%;
  height: 100px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.v-bar-fill {
  width: 24px;
  background: linear-gradient(to top, #DC143C, #ff4d6d);
  border-radius: 3px 3px 0 0;
  transition: height 0.5s ease;
  min-height: 4px;
}

.v-bar-value {
  font-size: 12px;
  font-weight: 600;
  color: #DC143C;
}

.v-bar-label {
  font-size: 9px;
  color: #6a7a8a;
}

/* 环形进度图 */
.exception-dashboard {
  display: flex;
  align-items: center;
  gap: 20px;
}

.exception-progress-ring {
  flex-shrink: 0;
}

.ring-bg,
.ring-progress {
  transition: stroke-dasharray 0.5s ease;
}

.exception-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-dot.pending {
  background: #ffc107;
}

.legend-dot.processing {
  background: #17a2b8;
}

.legend-dot.completed {
  background: #28a745;
}

.legend-text {
  font-size: 12px;
  color: #8a9ab0;
}

@media (max-width: 768px) {
  .top-stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
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

<template>
  <div class="exception-container">
    <div class="page-header">
      <h1>异常件查询</h1>
      <button class="refresh-btn" @click="fetchExceptions" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-number">{{ pendingCount }}</span>
        <span class="stat-label">待处理</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ processingCount }}</span>
        <span class="stat-label">处理中</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ completedCount }}</span>
        <span class="stat-label">已处理</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ exceptions.length }}</span>
        <span class="stat-label">全部异常</span>
      </div>
    </div>

    <!-- 异常件列表表格 -->
    <div class="table-container">
      <table class="exception-table">
        <thead>
          <tr>
            <th>异常件ID</th>
            <th>快递单号</th>
            <th>异常类型</th>
            <th>异常原因</th>
            <th>报告人</th>
            <th>报告时间</th>
            <th>来源</th>
            <th>处理状态</th>
            <th>处理人</th>
            <th>处理时间</th>
            <th>处理结果</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in exceptions" :key="item.id">
            <td>{{ item.id }}</td>
            <td class="tracking-number">{{ item.trackingNumber }}</td>
            <td>
              <span :class="['type-badge', getTypeClass(item.exceptionType)]">
                {{ item.exceptionType }}
              </span>
            </td>
            <td class="reason-cell" :title="item.exceptionReason">
              {{ item.exceptionReason || '-' }}
            </td>
            <td>{{ item.reportEmployeeName || '-' }}</td>
            <td>{{ formatDate(item.reportTime) }}</td>
            <td>
              <span :class="['source-badge', item.source === 'pickup' ? 'source-pickup' : 'source-verification']">
                {{ getSourceText(item.source) }}
              </span>
            </td>
            <td>
              <span :class="['status-badge', getStatusClass(item.handleStatus)]">
                {{ item.handleStatus || '待处理' }}
              </span>
            </td>
            <td>{{ item.handleEmployeeId || '-' }}</td>
            <td>{{ formatDate(item.handleTime) }}</td>
            <td class="result-cell" :title="item.handleResult">
              {{ item.handleResult || '-' }}
            </td>
          </tr>
          <tr v-if="exceptions.length === 0 && !loading">
            <td colspan="11" class="empty-message">
              暂无异常件记录
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
  name: 'EmployeeExceptionView',
  setup() {
    const exceptions = ref([])
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')

    // 计算各类状态数量
    const pendingCount = computed(() => 
      exceptions.value.filter(e => e.handleStatus === '待处理').length
    )
    const processingCount = computed(() => 
      exceptions.value.filter(e => e.handleStatus === '处理中').length
    )
    const completedCount = computed(() => 
      exceptions.value.filter(e => e.handleStatus === '已处理').length
    )

    // 获取异常件列表
    const fetchExceptions = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const response = await api.getAllExceptionPackages()
        if (response.data.success) {
          exceptions.value = response.data.data
        } else {
          errorMessage.value = response.data.message || '获取异常件列表失败'
        }
      } catch (error) {
        console.error('获取异常件列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取异常件列表失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    // 获取来源文本
    const getSourceText = (source) => {
      const sourceMap = {
        'pickup': '取件异常',
        'verification': '核验异常'
      }
      return sourceMap[source] || source || '-'
    }

    // 获取异常类型样式
    const getTypeClass = (type) => {
      const typeMap = {
        '收件人信息错误': 'type-info',
        '收件人未取件': 'type-info',
        '包裹信息不符': 'type-info',
        '包裹破损': 'type-damage',
        '包裹丢失': 'type-lost',
        '包裹错发': 'type-info',
        '重量异常': 'type-weight',
        '物品违禁': 'type-forbidden',
        '其他原因': 'type-other'
      }
      return typeMap[type] || 'type-other'
    }

    // 获取处理状态样式
    const getStatusClass = (status) => {
      const statusMap = {
        '待处理': 'status-pending',
        '处理中': 'status-processing',
        '已处理': 'status-completed'
      }
      return statusMap[status] || 'status-pending'
    }

    // 页面加载时获取数据
    onMounted(() => {
      fetchExceptions()
    })

    return {
      exceptions,
      loading,
      successMessage,
      errorMessage,
      pendingCount,
      processingCount,
      completedCount,
      fetchExceptions,
      formatDate,
      getSourceText,
      getTypeClass,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.exception-container {
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

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.exception-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1200px;
}

.exception-table th {
  background: #DC143C;
  color: white;
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap;
}

.exception-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  color: #333;
  font-size: 14px;
}

.exception-table tr:hover {
  background: #fff5f5;
}

.exception-table tr:last-child td {
  border-bottom: none;
}

.tracking-number {
  font-family: monospace;
  font-weight: 600;
  color: #DC143C;
}

.reason-cell,
.result-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 异常类型标签 */
.type-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.type-info {
  background: #E3F2FD;
  color: #1565C0;
}

.type-damage {
  background: #FFF3E0;
  color: #E65100;
}

.type-lost {
  background: #FFEBEE;
  color: #C62828;
}

.type-weight {
  background: #FFF8E1;
  color: #F57F17;
}

.type-forbidden {
  background: #F3E5F5;
  color: #7B1FA2;
}

.type-other {
  background: #ECEFF1;
  color: #546E7A;
}

/* 来源标签 */
.source-badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
}

.source-pickup {
  background: #E0F7FA;
  color: #00838F;
}

.source-verification {
  background: #FCE4EC;
  color: #C2185B;
}

/* 处理状态标签 */
.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #FFF3CD;
  color: #856404;
}

.status-processing {
  background: #CCE5FF;
  color: #004085;
}

.status-completed {
  background: #D4EDDA;
  color: #155724;
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

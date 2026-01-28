<template>
  <div class="outbound-container">
    <div class="page-header">
      <h1>快递出库</h1>
      <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 包裹列表表格 -->
    <div class="table-container">
      <table class="package-table">
        <thead>
          <tr>
            <th>快递单号</th>
            <th>收件人</th>
            <th>收件人电话</th>
            <th>收件地址</th>
            <th>包裹类型</th>
            <th>重量(kg)</th>
            <th>入库时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in packages" :key="pkg.id">
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td>{{ pkg.receiverName }}</td>
            <td>{{ pkg.receiverPhone }}</td>
            <td class="address">{{ pkg.receiverAddress }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td>{{ formatDate(pkg.entryTime) }}</td>
            <td>
              <span class="status-badge status-success">{{ pkg.status }}</span>
            </td>
            <td class="actions">
              <button
                class="btn btn-primary"
                @click="handleOutbound(pkg.id)"
                :disabled="processingId === pkg.id"
              >
                {{ processingId === pkg.id ? '处理中...' : '出库' }}
              </button>
            </td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td colspan="9" class="empty-message">
              暂无已入库的包裹
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
import { ref, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'EmployeeOutboundView',
  setup() {
    const packages = ref([])
    const loading = ref(false)
    const processingId = ref(null)
    const successMessage = ref('')
    const errorMessage = ref('')

    // 获取当前登录员工信息
    const getCurrentEmployeeId = () => {
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
      return userInfo.id || 1
    }

    // 获取已入库的包裹列表
    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const response = await api.getInStockPackages()
        if (response.data.success) {
          packages.value = response.data.data
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

    // 出库操作
    const handleOutbound = async (packageId) => {
      processingId.value = packageId
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const employeeId = getCurrentEmployeeId()
        const response = await api.outboundPackage(packageId, employeeId)

        if (response.data.success) {
          const result = response.data.data
          successMessage.value = `出库成功！已分配给员工：${result.deliveryEmployeeName}`
          // 移除已出库的包裹
          packages.value = packages.value.filter(p => p.id !== packageId)
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)
        } else {
          errorMessage.value = response.data.message || '出库失败'
        }
      } catch (error) {
        console.error('出库失败:', error)
        errorMessage.value = error.response?.data?.message || '出库失败，请稍后重试'
      } finally {
        processingId.value = null
      }
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    // 页面加载时获取数据
    onMounted(() => {
      fetchPackages()
    })

    return {
      packages,
      loading,
      processingId,
      successMessage,
      errorMessage,
      fetchPackages,
      handleOutbound,
      formatDate
    }
  }
}
</script>

<style scoped>
.outbound-container {
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

.table-container {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.package-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.package-table th {
  background: #DC143C;
  color: white;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap;
}

.package-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee;
  color: #333;
}

.package-table tr:hover {
  background: #fff5f5;
}

.package-table tr:last-child td {
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

.status-success {
  background: #d4edda;
  color: #155724;
}

.actions {
  white-space: nowrap;
}

.btn {
  padding: 8px 16px;
  border: 2px solid;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.btn-primary {
  background: #DC143C;
  color: white;
  border-color: #DC143C;
}

.btn-primary:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

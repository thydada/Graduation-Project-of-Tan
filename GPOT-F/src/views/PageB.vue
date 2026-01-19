<template>
  <div class="verification-container">
    <div class="page-header">
      <h1>快递审核情况</h1>
      <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 快递列表表格 -->
    <div class="table-container">
      <table class="package-table">
        <thead>
          <tr>
            <th>快递单号</th>
            <th>寄件人</th>
            <th>寄件人电话</th>
            <th>寄件地址</th>
            <th>包裹类型</th>
            <th>重量(kg)</th>
            <th>入库时间</th>
            <th>核验状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in packages" :key="pkg.id">
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td>{{ pkg.senderName }}</td>
            <td>{{ pkg.senderPhone }}</td>
            <td class="address">{{ pkg.senderAddress }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td>{{ formatDate(pkg.entryTime) }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(pkg.verificationSuccess)]">
                {{ getStatusText(pkg.verificationSuccess) }}
              </span>
            </td>
            <td class="actions">
              <button
                class="btn btn-success"
                @click="verificationPackage(pkg.id, 1)"
                :disabled="processingId === pkg.id"
              >
                核验成功
              </button>
              <button
                class="btn btn-error"
                @click="verificationPackage(pkg.id, 2)"
                :disabled="processingId === pkg.id"
              >
                核验出错
              </button>
            </td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td colspan="9" class="empty-message">
              暂无待核验的快递
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
  name: 'PageB',
  setup() {
    const packages = ref([])
    const loading = ref(false)
    const processingId = ref(null)
    const successMessage = ref('')
    const errorMessage = ref('')

    // 获取待核验的快递列表
    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const response = await api.getVerificationPendingPackages()
        if (response.data.success) {
          packages.value = response.data.data
        } else {
          errorMessage.value = response.data.message || '获取快递列表失败'
        }
      } catch (error) {
        console.error('获取快递列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取快递列表失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    // 核验快递
    const verificationPackage = async (id, status) => {
      processingId.value = id
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const response = await api.verificationPackage(id, status)
        if (response.data.success) {
          // 更新成功后移除该快递或更新状态
          const pkg = packages.value.find(p => p.id === id)
          if (pkg) {
            pkg.verificationSuccess = status
          }
          successMessage.value = response.data.message

          // 1.5秒后自动移除已处理的快递
          setTimeout(() => {
            packages.value = packages.value.filter(p => p.id !== id)
          }, 1500)
        } else {
          errorMessage.value = response.data.message || '核验失败'
        }
      } catch (error) {
        console.error('核验失败:', error)
        errorMessage.value = error.response?.data?.message || '核验失败，请稍后重试'
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

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待核验',
        1: '核验成功',
        2: '核验出错'
      }
      return statusMap[status] || '未知'
    }

    // 获取状态样式类
    const getStatusClass = (status) => {
      const classMap = {
        0: 'status-pending',
        1: 'status-success',
        2: 'status-error'
      }
      return classMap[status] || ''
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
      verificationPackage,
      formatDate,
      getStatusText,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.verification-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #F5F5F5 0%, #E8E8E8 100%);
  padding: 24px;
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
  background: #1E90FF;
  color: white;
  border: 2px solid #1E90FF;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: #1873CC;
  border-color: #1873CC;
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
  background: #1E90FF;
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
  background: #f0f8ff;
}

.package-table tr:last-child td {
  border-bottom: none;
}

.tracking-number {
  font-family: monospace;
  font-weight: 600;
  color: #1E90FF;
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

.actions {
  white-space: nowrap;
}

.btn {
  padding: 8px 16px;
  margin-right: 8px;
  border: 2px solid;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn:last-child {
  margin-right: 0;
}

.btn-success {
  background: #28a745;
  color: white;
  border-color: #28a745;
}

.btn-success:hover:not(:disabled) {
  background: #218838;
  border-color: #218838;
}

.btn-error {
  background: #dc3545;
  color: white;
  border-color: #dc3545;
}

.btn-error:hover:not(:disabled) {
  background: #c82333;
  border-color: #c82333;
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

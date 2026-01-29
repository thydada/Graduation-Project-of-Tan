<template>
  <div class="all-packages-container">
    <div class="page-header">
      <h1>全部包裹列表</h1>
      <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <div class="table-container">
      <table class="package-table">
        <thead>
          <tr>
            <th>包裹ID</th>
            <th>快递单号</th>
            <th>寄件人</th>
            <th>寄件人电话</th>
            <th>收件人</th>
            <th>收件人电话</th>
            <th>包裹类型</th>
            <th>重量(kg)</th>
            <th>尺寸</th>
            <th>状态</th>
            <th>货架</th>
            <th>层数</th>
            <th>取件码</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in packages" :key="pkg.id">
            <td>{{ pkg.id }}</td>
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td>{{ pkg.senderName }}</td>
            <td>{{ pkg.senderPhone }}</td>
            <td>{{ pkg.receiverName }}</td>
            <td>{{ pkg.receiverPhone }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td>{{ pkg.size }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(pkg.status)]">
                {{ pkg.status }}
              </span>
            </td>
            <td>{{ pkg.shelfId || '-' }}</td>
            <td>{{ pkg.shelfLayer || '-' }}</td>
            <td class="pickup-code">{{ pkg.pickupCode || '-' }}</td>
            <td>{{ formatDate(pkg.createTime) }}</td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td colspan="14" class="empty-message">
              暂无包裹数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'EmployeeAllPackagesView',
  setup() {
    const packages = ref([])
    const loading = ref(false)
    const errorMessage = ref('')

    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      try {
        const response = await api.getAllPackages()
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

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const getStatusClass = (status) => {
      if (status === '已入库') return 'status-success'
      if (status === '待入库' || status === '待取件') return 'status-pending'
      if (status === '异常') return 'status-error'
      return ''
    }

    onMounted(() => {
      fetchPackages()
    })

    return {
      packages,
      loading,
      errorMessage,
      fetchPackages,
      formatDate,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.all-packages-container {
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
  min-width: 1200px;
}

.package-table th {
  background: #DC143C;
  color: white;
  padding: 14px 12px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap;
}

.package-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  color: #333;
  font-size: 14px;
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

.pickup-code {
  font-family: monospace;
  color: #555;
}

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

.status-success {
  background: #D4EDDA;
  color: #155724;
}

.status-error {
  background: #F8D7DA;
  color: #721C24;
}

.empty-message {
  text-align: center;
  padding: 48px !important;
  color: #999;
  font-size: 16px;
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


<template>
  <div class="package-container">
    <div class="page-header">
      <h1>{{ pageTitle }}</h1>
      <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
        {{ loading ? '刷新中...' : '刷新列表' }}
      </button>
    </div>

    <!-- 快递列表表格 -->
    <div class="table-container">
      <table class="package-table">
        <thead>
          <tr>
            <th v-for="header in tableHeaders" :key="header.key">{{ header.label }}</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in packages" :key="pkg.id">
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td v-if="isDepartmentA">{{ pkg.receiverName }}</td>
            <td v-else>{{ pkg.senderName }}</td>
            <td v-if="isDepartmentA">{{ pkg.receiverPhone }}</td>
            <td v-else>{{ pkg.senderPhone }}</td>
            <td v-if="isDepartmentA" class="address">{{ pkg.receiverAddress }}</td>
            <td v-else class="address">{{ pkg.senderAddress }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td v-if="isDepartmentA">{{ formatDate(pkg.pickupDeadline) }}</td>
            <td v-else>{{ formatDate(pkg.entryTime) }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(pkg)]">
                {{ getStatusText(pkg) }}
              </span>
            </td>
            <td class="actions">
              <button
                class="btn btn-success"
                @click="handleSuccess(pkg.id)"
                :disabled="processingId === pkg.id"
              >
                {{ isDepartmentA ? '已取件' : '核验成功' }}
              </button>
              <button
                class="btn btn-error"
                @click="openExceptionModal(pkg)"
                :disabled="processingId === pkg.id"
              >
                {{ isDepartmentA ? '取件出错' : '核验出错' }}
              </button>
            </td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td :colspan="tableHeaders.length + 1" class="empty-message">
              {{ emptyMessage }}
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

    <!-- 异常报告弹窗 -->
    <div v-if="showExceptionModal" class="modal-overlay" @click.self="closeExceptionModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isDepartmentA ? '报告取件异常' : '报告核验异常' }}</h3>
          <button class="close-btn" @click="closeExceptionModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>快递单号：</label>
            <span class="tracking-info">{{ currentExceptionPackage?.trackingNumber }}</span>
          </div>
          <div class="form-group">
            <label for="exceptionType">异常类型：</label>
            <select id="exceptionType" v-model="exceptionType" class="form-control">
              <option value="">请选择异常类型</option>
              <option v-for="type in exceptionTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="exceptionReason">异常原因：</label>
            <textarea
              id="exceptionReason"
              v-model="exceptionReason"
              class="form-control"
              rows="4"
              placeholder="请详细描述异常情况..."
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="closeExceptionModal">取消</button>
          <button
            class="btn btn-submit"
            @click="submitException"
            :disabled="!exceptionType || submitting"
          >
            {{ submitting ? '提交中...' : '确认提交' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'

export default {
  name: 'EmployeePackageView',
  setup() {
    const packages = ref([])
    const loading = ref(false)
    const processingId = ref(null)
    const successMessage = ref('')
    const errorMessage = ref('')

    // 异常弹窗相关
    const showExceptionModal = ref(false)
    const currentExceptionPackage = ref(null)
    const exceptionType = ref('')
    const exceptionReason = ref('')
    const submitting = ref(false)

    // 获取当前登录员工信息
    const getCurrentEmployeeId = () => {
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
      return userInfo.id || 1
    }

    // 获取当前部门
    const isDepartmentA = computed(() => {
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
      return userInfo.department === 'A'
    })

    // 页面标题
    const pageTitle = computed(() => {
      return isDepartmentA.value ? '快递取得情况' : '快递审核情况'
    })

    // 空数据提示
    const emptyMessage = computed(() => {
      return isDepartmentA.value ? '暂无待审核的快递' : '暂无待核验的快递'
    })

    // 表格表头
    const tableHeaders = computed(() => {
      if (isDepartmentA.value) {
        return [
          { key: 'trackingNumber', label: '快递单号' },
          { key: 'receiverName', label: '收件人' },
          { key: 'receiverPhone', label: '收件人电话' },
          { key: 'receiverAddress', label: '收件地址' },
          { key: 'packageType', label: '包裹类型' },
          { key: 'weight', label: '重量(kg)' },
          { key: 'pickupDeadline', label: '取件期限' },
          { key: 'status', label: '状态' }
        ]
      } else {
        return [
          { key: 'trackingNumber', label: '快递单号' },
          { key: 'senderName', label: '寄件人' },
          { key: 'senderPhone', label: '寄件人电话' },
          { key: 'senderAddress', label: '寄件地址' },
          { key: 'packageType', label: '包裹类型' },
          { key: 'weight', label: '重量(kg)' },
          { key: 'entryTime', label: '入库时间' },
          { key: 'status', label: '核验状态' }
        ]
      }
    })

    // 异常类型选项
    const exceptionTypes = computed(() => {
      if (isDepartmentA.value) {
        return [
          '收件人信息错误',
          '收件人未取件',
          '包裹破损',
          '包裹丢失',
          '包裹错发',
          '其他原因'
        ]
      } else {
        return [
          '包裹信息不符',
          '包裹破损',
          '包裹丢失',
          '重量异常',
          '物品违禁',
          '其他原因'
        ]
      }
    })

    // 获取快递列表
    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        let response
        if (isDepartmentA.value) {
          // 部门A：获取待审核的快递列表
          response = await api.getPendingPackages()
        } else {
          // 部门B：获取待核验的快递列表
          response = await api.getVerificationPendingPackages()
        }

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

    // 处理成功操作
    const handleSuccess = async (id) => {
      processingId.value = id
      errorMessage.value = ''
      successMessage.value = ''

      try {
        let response
        const employeeId = getCurrentEmployeeId()

        if (isDepartmentA.value) {
          // 部门A：审核快递取件
          response = await api.verifyPackage(id, 1)
        } else {
          // 部门B：核验快递入库
          response = await api.verificationPackage(id, 1, employeeId, 1, 1)
        }

        if (response.data.success) {
          // 成功后移除该快递
          packages.value = packages.value.filter(p => p.id !== id)
          successMessage.value = response.data.message || (isDepartmentA.value ? '审核成功' : '核验成功，包裹已入库')
        } else {
          errorMessage.value = response.data.message || '操作失败'
        }
      } catch (error) {
        console.error('操作失败:', error)
        errorMessage.value = error.response?.data?.message || '操作失败，请稍后重试'
      } finally {
        processingId.value = null
      }
    }

    // 打开异常弹窗
    const openExceptionModal = (pkg) => {
      currentExceptionPackage.value = pkg
      exceptionType.value = ''
      exceptionReason.value = ''
      showExceptionModal.value = true
    }

    // 关闭异常弹窗
    const closeExceptionModal = () => {
      showExceptionModal.value = false
      currentExceptionPackage.value = null
      exceptionType.value = ''
      exceptionReason.value = ''
    }

    // 提交异常报告
    const submitException = async () => {
      if (!exceptionType.value || !currentExceptionPackage.value) {
        return
      }

      submitting.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const employeeId = getCurrentEmployeeId()
        const source = isDepartmentA.value ? 'pickup' : 'verification'

        const response = await api.reportException(
          currentExceptionPackage.value.id,
          exceptionType.value,
          exceptionReason.value,
          employeeId,
          source
        )

        if (response.data.success) {
          // 移除已处理的快递
          packages.value = packages.value.filter(p => p.id !== currentExceptionPackage.value.id)
          successMessage.value = response.data.message
          closeExceptionModal()
        } else {
          errorMessage.value = response.data.message || '提交异常报告失败'
        }
      } catch (error) {
        console.error('提交异常报告失败:', error)
        errorMessage.value = error.response?.data?.message || '提交异常报告失败，请稍后重试'
      } finally {
        submitting.value = false
      }
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    // 获取状态文本
    const getStatusText = (pkg) => {
      if (isDepartmentA.value) {
        const statusMap = {
          0: '待取件',
          1: '已取件',
          2: '取件出错'
        }
        return statusMap[pkg.pickupSuccess] || '未知'
      } else {
        const statusMap = {
          0: '待核验',
          1: '核验成功',
          2: '核验出错'
        }
        return statusMap[pkg.verificationSuccess] || '未知'
      }
    }

    // 获取状态样式类
    const getStatusClass = (pkg) => {
      const status = isDepartmentA.value ? pkg.pickupSuccess : pkg.verificationSuccess
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
      pageTitle,
      emptyMessage,
      tableHeaders,
      isDepartmentA,
      exceptionTypes,
      fetchPackages,
      handleSuccess,
      formatDate,
      getStatusText,
      getStatusClass,
      // 异常弹窗相关
      showExceptionModal,
      currentExceptionPackage,
      exceptionType,
      exceptionReason,
      submitting,
      openExceptionModal,
      closeExceptionModal,
      submitException
    }
  }
}
</script>

<style scoped>
.package-container {
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
  border-radius: 0;
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

/* 弹窗样式 */
.modal-overlay {
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

.modal-content {
  background: white;
  border-radius: 12px;
  width: 480px;
  max-width: 90%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
}

.tracking-info {
  font-family: monospace;
  color: #DC143C;
  font-weight: 600;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.2);
}

textarea.form-control {
  resize: vertical;
  min-height: 80px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #e5e5e5;
}

.btn-submit {
  padding: 10px 20px;
  background: #DC143C;
  color: white;
  border: 1px solid #DC143C;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

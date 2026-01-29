<template>
  <div class="verification-container">
    <div class="page-header">
      <h1>快递入库</h1>
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
            <th>入库状态</th>
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
                入库成功
              </button>
              <button
                class="btn btn-error"
                @click="openExceptionModal(pkg)"
                :disabled="processingId === pkg.id"
              >
                入库出错
              </button>
            </td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td colspan="9" class="empty-message">
              暂无待入库的快递
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
          <h3>报告入库异常</h3>
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
              <option value="包裹信息不符">包裹信息不符</option>
              <option value="包裹破损">包裹破损</option>
              <option value="包裹丢失">包裹丢失</option>
              <option value="重量异常">重量异常</option>
              <option value="物品违禁">物品违禁</option>
              <option value="其他原因">其他原因</option>
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

    // 获取待入库的快递列表
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

    // 入库快递 - 员工点击入库成功后执行完整入库流程
    // 1. 将快递信息写入 package 表
    // 2. 在 package_entry 表留下入库记录
    // 3. 从 package_temp 表删除该条数据
    // 仓库ID和货架ID默认填1
    const verificationPackage = async (id, status) => {
      processingId.value = id
      errorMessage.value = ''
      successMessage.value = ''

      try {
        // 调用后端API，传递当前员工ID，仓库ID和货架ID默认传1
        const employeeId = getCurrentEmployeeId()
        const response = await api.verificationPackage(id, status, employeeId, 1, 1)
        if (response.data.success) {
          // 入库成功后移除该快递
          packages.value = packages.value.filter(p => p.id !== id)
          successMessage.value = response.data.message || '入库成功'
        } else {
          errorMessage.value = response.data.message || '入库失败'
        }
      } catch (error) {
        console.error('入库失败:', error)
        errorMessage.value = error.response?.data?.message || '入库失败，请稍后重试'
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
        const response = await api.reportException(
          currentExceptionPackage.value.id,
          exceptionType.value,
          exceptionReason.value,
          1, // 默认员工ID
          'verification' // 来源：入库异常
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
    const getStatusText = (status) => {
      const statusMap = {
        0: '待入库',
        1: '入库成功',
        2: '入库出错'
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
  color: #1E90FF;
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
  border-color: #1E90FF;
  box-shadow: 0 0 0 2px rgba(30, 144, 255, 0.2);
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
  background: #1E90FF;
  color: white;
  border: 1px solid #1E90FF;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #1873CC;
  border-color: #1873CC;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

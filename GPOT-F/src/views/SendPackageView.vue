<template>
  <div class="send-package-container">
    <div class="form-card">
      <div class="form-header">
        <h2 class="form-title">我要寄件</h2>
        <p class="form-subtitle">请填写寄件信息，我们将为您提供优质的快递服务</p>
      </div>

      <form @submit.prevent="handleSubmit" class="send-package-form">
        <!-- 寄件人信息 -->
        <div class="form-section">
          <h3 class="section-title">寄件人信息</h3>
          <div class="form-row">
            <div class="form-group">
              <label for="senderName" class="form-label">寄件人姓名 *</label>
              <input
                id="senderName"
                v-model="form.senderName"
                type="text"
                class="form-input"
                placeholder="请输入寄件人姓名"
                required
              >
            </div>
            <div class="form-group">
              <label for="senderPhone" class="form-label">寄件人电话 *</label>
              <input
                id="senderPhone"
                v-model="form.senderPhone"
                type="tel"
                class="form-input"
                placeholder="请输入寄件人电话"
                required
              >
            </div>
          </div>
          <div class="form-group">
            <label for="senderAddress" class="form-label">寄件人地址 *</label>
            <textarea
              id="senderAddress"
              v-model="form.senderAddress"
              class="form-textarea"
              placeholder="请输入详细地址（省市区县街道门牌号）"
              rows="3"
              required
            ></textarea>
          </div>
        </div>

        <!-- 收件人信息 -->
        <div class="form-section">
          <h3 class="section-title">收件人信息</h3>
          <div class="form-row">
            <div class="form-group">
              <label for="receiverName" class="form-label">收件人姓名 *</label>
              <input
                id="receiverName"
                v-model="form.receiverName"
                type="text"
                class="form-input"
                placeholder="请输入收件人姓名"
                required
              >
            </div>
            <div class="form-group">
              <label for="receiverPhone" class="form-label">收件人电话 *</label>
              <input
                id="receiverPhone"
                v-model="form.receiverPhone"
                type="tel"
                class="form-input"
                placeholder="请输入收件人电话"
                required
              >
            </div>
          </div>
          <div class="form-group">
            <label for="receiverAddress" class="form-label">收件人地址 *</label>
            <textarea
              id="receiverAddress"
              v-model="form.receiverAddress"
              class="form-textarea"
              placeholder="请输入详细地址（省市区县街道门牌号）"
              rows="3"
              required
            ></textarea>
          </div>
        </div>

        <!-- 包裹信息 -->
        <div class="form-section">
          <h3 class="section-title">包裹信息</h3>
          <div class="form-row">
            <div class="form-group">
              <label for="packageType" class="form-label">包裹类型 *</label>
              <select
                id="packageType"
                v-model="form.packageType"
                class="form-select"
                required
              >
                <option value="">请选择包裹类型</option>
                <option value="文件">文件</option>
                <option value="数码产品">数码产品</option>
                <option value="服装">服装</option>
                <option value="食品">食品</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-group">
              <label for="weight" class="form-label">重量 (kg) *</label>
              <input
                id="weight"
                v-model.number="form.weight"
                type="number"
                class="form-input"
                placeholder="请输入重量"
                step="0.1"
                min="0.1"
                max="50"
                required
              >
            </div>
          </div>
          <div class="form-group">
            <label for="size" class="form-label">尺寸</label>
            <input
              id="size"
              v-model="form.size"
              type="text"
              class="form-input"
              placeholder="例如：30x20x10cm"
            >
          </div>
        </div>

        <!-- 表单操作 -->
        <div class="form-actions">
          <button type="button" @click="resetForm" class="btn-secondary">
            重置
          </button>
          <button type="submit" :disabled="loading" class="btn-primary">
            <span v-if="loading" class="loading-spinner">⏳</span>
            {{ loading ? '提交中...' : '确认寄件' }}
          </button>
        </div>
      </form>

      <!-- 提交结果提示 -->
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>

      <!-- 寄件成功提示 -->
      <div v-if="successData" class="success-card">
        <div class="success-icon">✅</div>
        <h3>寄件成功！</h3>
        <div class="success-details">
          <p><strong>快递单号：</strong>{{ successData.trackingNumber }}</p>
          <p><strong>状态：</strong>{{ successData.status }}</p>
          <p><strong>创建时间：</strong>{{ formatDate(successData.createTime) }}</p>
        </div>
        <button @click="resetAfterSuccess" class="btn-success">
          继续寄件
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import apiService from '../services/api'

export default {
  name: 'SendPackageView',
  setup() {
    const loading = ref(false)
    const message = ref('')
    const messageType = ref('')
    const successData = ref(null)

    const form = reactive({
      senderName: '',
      senderPhone: '',
      senderAddress: '',
      receiverName: '',
      receiverPhone: '',
      receiverAddress: '',
      packageType: '',
      weight: null,
      size: ''
    })

    const resetForm = () => {
      Object.keys(form).forEach(key => {
        if (typeof form[key] === 'string') {
          form[key] = ''
        } else if (typeof form[key] === 'number') {
          form[key] = null
        }
      })
      message.value = ''
      messageType.value = ''
    }

    const resetAfterSuccess = () => {
      resetForm()
      successData.value = null
    }

    const handleSubmit = async () => {
      loading.value = true
      message.value = ''
      messageType.value = ''

      try {
        // 从localStorage获取用户ID
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.id) {
          throw new Error('用户未登录')
        }

        const requestData = {
          ...form,
          userId: user.id
        }

        const response = await apiService.sendPackage(requestData)

        if (response.data.success) {
          successData.value = response.data.data
          message.value = '寄件成功！'
          messageType.value = 'success'
        } else {
          throw new Error(response.data.message || '寄件失败')
        }

      } catch (error) {
        message.value = error.response?.data?.message || error.message || '寄件失败，请重试'
        messageType.value = 'error'
        console.error('寄件失败:', error)
      } finally {
        loading.value = false
      }
    }

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString('zh-CN')
    }

    onMounted(() => {
      // 检查用户是否已登录
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) {
        // 未登录，跳转到登录页面
        window.location.href = '/'
      }
    })

    return {
      form,
      loading,
      message,
      messageType,
      successData,
      handleSubmit,
      resetForm,
      resetAfterSuccess,
      formatDate
    }
  }
}
</script>

<style scoped>
.send-package-container {
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.send-package-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.form-section {
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 24px;
}

.section-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #667eea;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  color: #333;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  padding-top: 24px;
  border-top: 1px solid #e9ecef;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f8f9fa;
  color: #666;
  border: 2px solid #e9ecef;
}

.btn-secondary:hover {
  background: #e9ecef;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.message {
  margin-top: 20px;
  padding: 12px 16px;
  border-radius: 8px;
  font-weight: 500;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.success-card {
  margin-top: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  border: 2px solid #28a745;
  border-radius: 12px;
  text-align: center;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.success-card h3 {
  color: #155724;
  margin-bottom: 20px;
  font-size: 24px;
}

.success-details {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.success-details p {
  margin: 8px 0;
  color: #333;
}

.btn-success {
  background: #28a745;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-success:hover {
  background: #218838;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-card {
    padding: 20px;
  }

  .form-title {
    font-size: 24px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }
}
</style>
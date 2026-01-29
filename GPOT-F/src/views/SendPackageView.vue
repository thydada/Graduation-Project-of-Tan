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
            <label class="form-label">尺寸 (cm)</label>
            <div class="size-inputs">
              <div class="size-input-group">
                <label for="length" class="size-label">长</label>
                <input
                  id="length"
                  v-model.number="form.length"
                  type="number"
                  class="form-input size-input"
                  placeholder="长度"
                  step="0.1"
                  min="0.1"
                >
                <span class="size-unit">cm</span>
              </div>
              <div class="size-input-group">
                <label for="width" class="size-label">宽</label>
                <input
                  id="width"
                  v-model.number="form.width"
                  type="number"
                  class="form-input size-input"
                  placeholder="宽度"
                  step="0.1"
                  min="0.1"
                >
                <span class="size-unit">cm</span>
              </div>
              <div class="size-input-group">
                <label for="height" class="size-label">高</label>
            <input
                  id="height"
                  v-model.number="form.height"
                  type="number"
                  class="form-input size-input"
                  placeholder="高度"
                  step="0.1"
                  min="0.1"
            >
                <span class="size-unit">cm</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 表单操作 -->
        <div class="form-actions">
          <button type="button" @click="resetForm" class="btn-secondary">
            重置
          </button>
          <button type="submit" :disabled="loading" class="btn-primary">
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
        <div class="success-icon">✓</div>
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
      length: null,
      width: null,
      height: null
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

        // 组合尺寸信息：长x宽x高
        let size = ''
        if (form.length && form.width && form.height) {
          size = `${form.length}x${form.width}x${form.height}`
        }

        const requestData = {
          senderName: form.senderName,
          senderPhone: form.senderPhone,
          senderAddress: form.senderAddress,
          receiverName: form.receiverName,
          receiverPhone: form.receiverPhone,
          receiverAddress: form.receiverAddress,
          packageType: form.packageType,
          weight: form.weight,
          size: size,
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
  max-width: 900px;
  margin: 0 auto;
}

.form-card {
  background: #ffffff;
  border: 2px solid #e0e0e0;
  border-radius: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 48px;
}

.form-header {
  text-align: center;
  margin-bottom: 48px;
  border-bottom: 3px solid #DC143C;
  padding-bottom: 24px;
}

.form-title {
  color: #333333;
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.form-subtitle {
  color: #666666;
  font-size: 16px;
  margin: 0;
  font-weight: 500;
}

.send-package-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.form-section {
  border: 2px solid #cccccc;
  border-radius: 0;
  padding: 32px;
  margin-bottom: 24px;
  background: #fafafa;
}

.section-title {
  color: #333333;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 3px solid #DC143C;
  letter-spacing: 0.5px;
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
  color: #333333;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 10px;
  display: block;
}

.form-input,
.form-select,
.form-textarea {
  padding: 14px 16px;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 16px;
  transition: border-color 0.2s ease;
  background: #ffffff;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.1);
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
  padding: 14px 28px;
  border: 2px solid;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.5px;
}

.btn-primary {
  background: #DC143C;
  border-color: #DC143C;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #ffffff;
  color: #333333;
  border-color: #cccccc;
}

.btn-secondary:hover {
  background: #f0f0f0;
  border-color: #999999;
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
  margin-top: 40px;
  padding: 36px;
  background: #ffffff;
  border: 3px solid #DC143C;
  border-radius: 0;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.success-icon {
  font-size: 56px;
  margin-bottom: 20px;
  color: #DC143C;
}

.success-card h3 {
  color: #333333;
  margin-bottom: 24px;
  font-size: 28px;
  font-weight: 700;
}

.success-details {
  background: #f8f8f8;
  padding: 24px;
  border-radius: 0;
  margin-bottom: 24px;
  border: 1px solid #cccccc;
}

.success-details p {
  margin: 10px 0;
  color: #333333;
  font-size: 16px;
}

.btn-success {
  background: #DC143C;
  color: white;
  padding: 14px 28px;
  border: 2px solid #DC143C;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.btn-success:hover {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.size-inputs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.size-input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.size-label {
  color: #333333;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 0;
}

.size-input {
  width: 100%;
}

.size-unit {
  color: #666666;
  font-size: 14px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-card {
    padding: 24px;
  }

  .form-title {
    font-size: 28px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .size-inputs {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary,
  .btn-success {
    width: 100%;
    justify-content: center;
  }

  .form-section {
    padding: 24px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>
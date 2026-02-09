<template>
  <div class="machine-container">
    <div class="machine-card">
      <div class="machine-header">
        <h1 class="machine-title">快递出库机器</h1>
        <button class="back-btn" @click="goBack">返回登录</button>
      </div>

      <div class="outbound-section">
        <div class="instruction-text">
          <p>请将快递包裹的快递单号输入下方，然后点击确认出库</p>
        </div>

        <div class="outbound-form">
          <div class="form-group">
            <label>快递单号</label>
            <input
              v-model="trackingNumber"
              type="text"
              placeholder="请输入快递单号"
              class="outbound-input"
              @keyup.enter="handleOutbound"
              :disabled="loading"
            />
          </div>

          <button
            class="outbound-btn"
            :disabled="loading || !trackingNumber.trim()"
            @click="handleOutbound"
          >
            {{ loading ? '处理中...' : '确认出库' }}
          </button>
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          <div class="success-icon">✓</div>
          <div class="success-content">
            <h3>出库成功！</h3>
            <p>{{ successMessage }}</p>
            <div v-if="outboundResult" class="result-details">
              <div class="detail-row">
                <span class="detail-label">快递单号：</span>
                <span class="detail-value">{{ outboundResult.trackingNumber }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">收件人：</span>
                <span class="detail-value">{{ outboundResult.receiverName }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">取件码：</span>
                <span class="detail-value pickup-code">{{ outboundResult.pickupCode || '暂无' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">状态：</span>
                <span class="detail-value status-badge status-success">{{ outboundResult.status }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

export default {
  name: 'OutboundMachineView',
  setup() {
    const router = useRouter()

    const trackingNumber = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const outboundResult = ref(null)

    const goBack = () => {
      router.push('/')
    }

    const handleOutbound = async () => {
      if (!trackingNumber.value.trim()) {
        errorMessage.value = '请输入快递单号'
        return
      }

      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''
      outboundResult.value = null

      try {
        const response = await api.userPickupPackage(trackingNumber.value.trim())
        if (response.data && response.data.success) {
          outboundResult.value = response.data.data
          successMessage.value = '快递已成功出库'
          trackingNumber.value = ''
          
          setTimeout(() => {
            successMessage.value = ''
            outboundResult.value = null
          }, 5000)
        } else {
          errorMessage.value = response.data?.message || '出库失败'
        }
      } catch (error) {
        console.error('出库失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '出库失败，请稍后重试'
        } else {
          errorMessage.value = '出库失败，请稍后重试'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      trackingNumber,
      loading,
      errorMessage,
      successMessage,
      outboundResult,
      goBack,
      handleOutbound
    }
  }
}
</script>

<style scoped>
.machine-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #F5F5F5 0%, #E8E8E8 100%);
  padding: 20px;
}

.machine-card {
  background: white;
  border-radius: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 48px;
  width: 100%;
  max-width: 600px;
  border: 3px solid #DC143C;
}

.machine-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 3px solid #DC143C;
}

.machine-title {
  color: #333333;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
  margin: 0;
}

.back-btn {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 0;
  border: 2px solid #cccccc;
  background-color: #fff;
  cursor: pointer;
  color: #666;
  transition: all 0.2s ease;
}

.back-btn:hover {
  border-color: #DC143C;
  color: #DC143C;
}

.outbound-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.instruction-text {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border: 2px solid #e0e0e0;
  border-radius: 0;
}

.instruction-text p {
  margin: 0;
  font-size: 16px;
  color: #555;
  line-height: 1.6;
}

.outbound-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #555;
  font-size: 16px;
}

.outbound-input {
  padding: 18px;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 20px;
  transition: border-color 0.2s ease;
  background: #ffffff;
  letter-spacing: 1px;
}

.outbound-input:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.1);
}

.outbound-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.outbound-btn {
  padding: 20px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 1px;
}

.outbound-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.outbound-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.error-message {
  padding: 16px;
  background: #fff3cd;
  border: 2px solid #ffc107;
  color: #856404;
  border-radius: 0;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
}

.success-message {
  padding: 24px;
  background: #d4edda;
  border: 2px solid #28a745;
  border-radius: 0;
  text-align: center;
}

.success-icon {
  font-size: 48px;
  color: #28a745;
  margin-bottom: 16px;
  font-weight: bold;
}

.success-content h3 {
  margin: 0 0 12px 0;
  color: #155724;
  font-size: 24px;
  font-weight: 700;
}

.success-content p {
  margin: 0 0 20px 0;
  color: #155724;
  font-size: 18px;
}

.result-details {
  margin-top: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #28a745;
  text-align: left;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 16px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #555;
  min-width: 100px;
}

.detail-value {
  color: #333;
  flex: 1;
}

.pickup-code {
  font-size: 18px;
  font-weight: 700;
  color: #DC143C;
  letter-spacing: 2px;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 0;
  font-weight: 600;
  font-size: 14px;
}

.status-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}
</style>

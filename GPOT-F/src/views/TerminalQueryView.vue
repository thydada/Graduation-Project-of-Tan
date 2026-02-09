<template>
  <div class="terminal-container">
    <div class="terminal-card">
      <div class="terminal-header">
        <h1 class="terminal-title">快递查询终端</h1>
        <button class="back-btn" @click="goBack">返回登录</button>
      </div>

      <div class="query-section">
        <div class="query-tabs">
          <button
            :class="['tab-btn', { active: queryType === 'phone' }]"
            @click="queryType = 'phone'"
          >
            电话号码查询
          </button>
          <button
            :class="['tab-btn', { active: queryType === 'tracking' }]"
            @click="queryType = 'tracking'"
          >
            快递单号查询
          </button>
        </div>

        <div class="query-form">
          <div v-if="queryType === 'phone'" class="form-group">
            <label>收件人电话号码</label>
            <input
              v-model="phoneNumber"
              type="text"
              placeholder="请输入收件人电话号码"
              class="query-input"
              @keyup.enter="handlePhoneQuery"
            />
          </div>

          <div v-if="queryType === 'tracking'" class="form-group">
            <label>快递单号</label>
            <input
              v-model="trackingNumber"
              type="text"
              placeholder="请输入快递单号"
              class="query-input"
              @keyup.enter="handleTrackingQuery"
            />
          </div>

          <button
            class="query-btn"
            :disabled="loading || (!phoneNumber && !trackingNumber)"
            @click="queryType === 'phone' ? handlePhoneQuery() : handleTrackingQuery()"
          >
            {{ loading ? '查询中...' : '查询' }}
          </button>
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </div>

      <div v-if="queryResults.length > 0" class="results-section">
        <h2 class="results-title">查询结果</h2>
        <div class="results-list">
          <div
            v-for="(pkg, index) in queryResults"
            :key="pkg.id || index"
            class="result-card"
          >
            <div class="result-header">
              <span class="result-label">快递单号：</span>
              <span class="result-value">{{ pkg.trackingNumber }}</span>
            </div>
            <div class="result-row">
              <span class="result-label">收件人：</span>
              <span class="result-value">{{ pkg.receiverName }}</span>
            </div>
            <div class="result-row">
              <span class="result-label">取件码：</span>
              <span class="result-value pickup-code">{{ pkg.pickupCode || '暂无' }}</span>
            </div>
            <div class="result-row">
              <span class="result-label">货架位置：</span>
              <span class="result-value shelf-location">
                {{ getShelfLocation(pkg) }}
              </span>
            </div>
            <div class="result-row">
              <span class="result-label">状态：</span>
              <span class="result-value status-badge" :class="getStatusClass(pkg.status)">
                {{ pkg.status }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="queryResults.length === 0 && hasSearched && !loading" class="no-results">
        未找到相关快递信息
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

export default {
  name: 'TerminalQueryView',
  setup() {
    const router = useRouter()

    const queryType = ref('phone')
    const phoneNumber = ref('')
    const trackingNumber = ref('')
    const loading = ref(false)
    const errorMessage = ref('')
    const queryResults = ref([])
    const hasSearched = ref(false)

    const goBack = () => {
      router.push('/')
    }

    const handlePhoneQuery = async () => {
      if (!phoneNumber.value.trim()) {
        errorMessage.value = '请输入电话号码'
        return
      }

      loading.value = true
      errorMessage.value = ''
      queryResults.value = []
      hasSearched.value = true

      try {
        const response = await api.getPackagesByReceiverPhone(phoneNumber.value.trim())
        if (response.data && response.data.success) {
          queryResults.value = response.data.data || []
          if (queryResults.value.length === 0) {
            errorMessage.value = '未找到该电话号码对应的快递'
          }
        } else {
          errorMessage.value = response.data?.message || '查询失败'
        }
      } catch (error) {
        console.error('查询失败:', error)
        if (error.response && error.response.status === 404) {
          errorMessage.value = '未找到该电话号码对应的快递'
          queryResults.value = []
        } else {
          errorMessage.value = error.response?.data?.message || '查询失败，请稍后重试'
        }
      } finally {
        loading.value = false
      }
    }

    const handleTrackingQuery = async () => {
      if (!trackingNumber.value.trim()) {
        errorMessage.value = '请输入快递单号'
        return
      }

      loading.value = true
      errorMessage.value = ''
      queryResults.value = []
      hasSearched.value = true

      try {
        const response = await api.getPackageByTrackingNumber(trackingNumber.value.trim())
        if (response.data && response.data.success) {
          const pkg = response.data.data
          if (pkg) {
            queryResults.value = [pkg]
          } else {
            errorMessage.value = '未找到该快递单号对应的快递'
          }
        } else {
          errorMessage.value = response.data?.message || '查询失败'
        }
      } catch (error) {
        console.error('查询失败:', error)
        if (error.response && error.response.status === 404) {
          errorMessage.value = '未找到该快递单号对应的快递'
          queryResults.value = []
        } else {
          errorMessage.value = error.response?.data?.message || '查询失败，请稍后重试'
        }
      } finally {
        loading.value = false
      }
    }

    const getShelfLocation = (pkg) => {
      if (pkg.shelfId && pkg.shelfLayer) {
        return `货架${pkg.shelfId} - 第${pkg.shelfLayer}层`
      }
      return '未分配'
    }

    const getStatusClass = (status) => {
      if (status === '已入库') {
        return 'status-in-stock'
      }
      return 'status-other'
    }

    return {
      queryType,
      phoneNumber,
      trackingNumber,
      loading,
      errorMessage,
      queryResults,
      hasSearched,
      goBack,
      handlePhoneQuery,
      handleTrackingQuery,
      getShelfLocation,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.terminal-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #F5F5F5 0%, #E8E8E8 100%);
  padding: 20px;
}

.terminal-card {
  background: white;
  border-radius: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 48px;
  width: 100%;
  max-width: 800px;
  border: 3px solid #DC143C;
}

.terminal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 3px solid #DC143C;
}

.terminal-title {
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

.query-section {
  margin-bottom: 32px;
}

.query-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tab-btn {
  flex: 1;
  padding: 14px;
  border: 2px solid #cccccc;
  background: white;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.tab-btn:hover:not(:disabled) {
  border-color: #DC143C;
}

.tab-btn.active {
  border-color: #DC143C;
  background: #DC143C;
  color: white;
}

.query-form {
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

.query-input {
  padding: 16px;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 18px;
  transition: border-color 0.2s ease;
  background: #ffffff;
}

.query-input:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.1);
}

.query-btn {
  padding: 18px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  border-radius: 0;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.query-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.query-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background: #fff3cd;
  border: 2px solid #ffc107;
  color: #856404;
  border-radius: 0;
  text-align: center;
  font-size: 14px;
}

.results-section {
  margin-top: 32px;
}

.results-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #DC143C;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-card {
  padding: 24px;
  border: 2px solid #cccccc;
  border-radius: 0;
  background: #fafafa;
  transition: all 0.2s ease;
}

.result-card:hover {
  border-color: #DC143C;
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.1);
}

.result-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e0e0;
}

.result-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 16px;
}

.result-label {
  font-weight: 600;
  color: #555;
  min-width: 100px;
}

.result-value {
  color: #333;
  flex: 1;
}

.pickup-code {
  font-size: 20px;
  font-weight: 700;
  color: #DC143C;
  letter-spacing: 2px;
}

.shelf-location {
  font-size: 18px;
  font-weight: 600;
  color: #DC143C;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 0;
  font-weight: 600;
  font-size: 14px;
}

.status-in-stock {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-other {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 18px;
}
</style>

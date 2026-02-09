<template>
  <div class="send-message-container">
    <div class="page-header">
      <h1>消息发送</h1>
    </div>

    <div class="message-form-card">
      <div class="form-section">
        <h2 class="section-title">编辑消息</h2>
        <p class="section-desc">填写消息内容后，将统一发送给所有用户</p>

        <div class="form-group">
          <label class="form-label">
            消息标题 <span class="required">*</span>
          </label>
          <input
            v-model="messageForm.title"
            type="text"
            class="form-input"
            placeholder="请输入消息标题"
            maxlength="200"
          />
          <span class="char-count">{{ messageForm.title.length }}/200</span>
        </div>

        <div class="form-group">
          <label class="form-label">
            消息类型 <span class="required">*</span>
          </label>
          <select v-model="messageForm.messageType" class="form-select">
            <option value="系统公告">系统公告</option>
            <option value="促销信息">促销信息</option>
            <option value="取件提醒">取件提醒</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">
            消息内容 <span class="required">*</span>
          </label>
          <textarea
            v-model="messageForm.content"
            class="form-textarea"
            placeholder="请输入消息内容"
            rows="8"
          ></textarea>
          <span class="char-count">{{ messageForm.content.length }} 字符</span>
        </div>

        <div class="form-actions">
          <button
            class="submit-btn"
            @click="handleSendMessage"
            :disabled="loading || !isFormValid"
          >
            {{ loading ? '发送中...' : '发送给所有用户' }}
          </button>
          <button
            class="reset-btn"
            @click="handleReset"
            :disabled="loading"
          >
            重置
          </button>
        </div>
      </div>
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import api from '../services/api'

export default {
  name: 'EmployeeSendMessageView',
  setup() {
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')

    const messageForm = ref({
      title: '',
      content: '',
      messageType: '系统公告'
    })

    const isFormValid = computed(() => {
      return messageForm.value.title.trim().length > 0 &&
             messageForm.value.content.trim().length > 0
    })

    const handleSendMessage = async () => {
      if (!isFormValid.value) {
        errorMessage.value = '请填写完整的消息信息'
        return
      }

      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.id) {
          errorMessage.value = '员工未登录'
          return
        }

        const requestData = {
          title: messageForm.value.title.trim(),
          content: messageForm.value.content.trim(),
          messageType: messageForm.value.messageType,
          senderType: 'employee',
          senderId: user.id,
          warehouseId: null
        }

        const response = await api.sendMessageToAllUsers(requestData)
        if (response.data.success) {
          const count = response.data.data.count || 0
          successMessage.value = `消息发送成功！已发送给 ${count} 个用户`
          handleReset()
          setTimeout(() => {
            successMessage.value = ''
          }, 5000)
        } else {
          errorMessage.value = response.data.message || '消息发送失败'
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        errorMessage.value = error.response?.data?.message || '消息发送失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    const handleReset = () => {
      messageForm.value = {
        title: '',
        content: '',
        messageType: '系统公告'
      }
      errorMessage.value = ''
    }

    return {
      loading,
      successMessage,
      errorMessage,
      messageForm,
      isFormValid,
      handleSendMessage,
      handleReset
    }
  }
}
</script>

<style scoped>
.send-message-container {
  min-height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.message-form-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 0;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-section {
  max-width: 800px;
}

.section-title {
  color: #333;
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #DC143C;
}

.section-desc {
  color: #666;
  font-size: 14px;
  margin: 0 0 24px 0;
}

.form-group {
  margin-bottom: 24px;
  position: relative;
}

.form-label {
  display: block;
  font-weight: 600;
  color: #555;
  font-size: 16px;
  margin-bottom: 8px;
}

.required {
  color: #DC143C;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 16px;
  transition: border-color 0.2s ease;
  background: #ffffff;
  font-family: inherit;
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
  min-height: 150px;
}

.char-count {
  display: block;
  text-align: right;
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e0e0e0;
}

.submit-btn {
  flex: 1;
  padding: 16px;
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

.submit-btn:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(220, 20, 60, 0.3);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.reset-btn {
  padding: 16px 32px;
  background: white;
  color: #666;
  border: 2px solid #cccccc;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.reset-btn:hover:not(:disabled) {
  border-color: #DC143C;
  color: #DC143C;
}

.reset-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.success-message {
  margin-top: 20px;
  padding: 16px;
  background: #d4edda;
  border: 2px solid #28a745;
  color: #155724;
  border-radius: 0;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
}

.error-message {
  margin-top: 20px;
  padding: 16px;
  background: #f8d7da;
  border: 2px solid #dc3545;
  color: #721c24;
  border-radius: 0;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
}
</style>

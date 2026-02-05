<template>
  <div class="debug-container">
    <div class="debug-card">
      <div class="debug-header">
        <h1>Debug 快递生成器</h1>
        <p>用于开发调试：直接向正式包裹表(package)写入测试快递数据。</p>
      </div>

      <form class="debug-form" @submit.prevent="handleSubmit">
        <h2>寄件人信息</h2>
        <div class="form-row">
          <label>寄件人姓名 *</label>
          <input v-model="form.senderName" type="text" required placeholder="例如：测试用户A" />
        </div>
        <div class="form-row">
          <label>寄件人电话 *</label>
          <input v-model="form.senderPhone" type="text" required placeholder="例如：18800001111" />
        </div>
        <div class="form-row">
          <label>寄件人地址</label>
          <input v-model="form.senderAddress" type="text" placeholder="例如：某某大学某某楼" />
        </div>

        <h2>收件人信息</h2>
        <div class="form-row">
          <label>收件人姓名 *</label>
          <input v-model="form.receiverName" type="text" required placeholder="例如：测试用户B" />
        </div>
        <div class="form-row">
          <label>收件人电话 *</label>
          <input v-model="form.receiverPhone" type="text" required placeholder="例如：18800002222" />
        </div>
        <div class="form-row">
          <label>收件人地址</label>
          <input v-model="form.receiverAddress" type="text" placeholder="例如：某某学院快递点" />
        </div>

        <h2>包裹信息</h2>
        <div class="form-row">
          <label>包裹类型 *</label>
          <select v-model="form.packageType" required>
            <option disabled value="">请选择类型</option>
            <option value="文件">文件</option>
            <option value="数码产品">数码产品</option>
            <option value="服装">服装</option>
            <option value="食品">食品</option>
            <option value="其他">其他</option>
          </select>
        </div>
        <div class="form-row">
          <label>重量(kg)</label>
          <input v-model.number="form.weight" type="number" min="0" step="0.01" placeholder="例如：1.5" />
        </div>
        <div class="form-row">
          <label>尺寸(长x宽x高, cm)</label>
          <input v-model="form.size" type="text" placeholder="例如：30x20x10" />
        </div>
        <div class="form-row">
          <label>状态</label>
          <select v-model="form.status">
            <option value="">默认：待入库</option>
            <option value="待入库">待入库</option>
            <option value="已入库">已入库</option>
            <option value="待取件">待取件</option>
            <option value="已取件">已取件</option>
            <option value="运输中">运输中</option>
          </select>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-secondary" @click="$router.push('/')">
            返回登录
          </button>
          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? '创建中...' : '生成测试快递' }}
          </button>
        </div>
      </form>

      <div v-if="successMessage" class="success-message">
        <p>{{ successMessage }}</p>
        <p v-if="createdTrackingNumber">
          快递单号：<span class="tracking">{{ createdTrackingNumber }}</span>
        </p>
      </div>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import api from '../services/api'

export default {
  name: 'DebugCreatePackageView',
  setup() {
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const createdTrackingNumber = ref('')

    const form = ref({
      senderName: '',
      senderPhone: '',
      senderAddress: '',
      receiverName: '',
      receiverPhone: '',
      receiverAddress: '',
      packageType: '',
      weight: null,
      size: '',
      status: ''
    })

    const handleSubmit = async () => {
      loading.value = true
      successMessage.value = ''
      errorMessage.value = ''
      createdTrackingNumber.value = ''

      try {
        const payload = {
          senderName: form.value.senderName,
          senderPhone: form.value.senderPhone,
          senderAddress: form.value.senderAddress,
          receiverName: form.value.receiverName,
          receiverPhone: form.value.receiverPhone,
          receiverAddress: form.value.receiverAddress,
          packageType: form.value.packageType,
          weight: form.value.weight,
          size: form.value.size,
          status: form.value.status || null,
          userId: null
        }

        const resp = await api.debugCreatePackage(payload)
        if (resp.data && resp.data.success) {
          const pkg = resp.data.data
          createdTrackingNumber.value = pkg.trackingNumber
          successMessage.value = '调试包裹创建成功！可以在员工“全部包裹”等页面查看。'
        } else {
          errorMessage.value = resp.data?.message || '创建失败'
        }
      } catch (e) {
        console.error('debugCreatePackage error', e)
        errorMessage.value = e.response?.data?.message || e.message || '创建失败'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      successMessage,
      errorMessage,
      createdTrackingNumber,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.debug-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 20px;
}

.debug-card {
  width: 100%;
  max-width: 720px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
  padding: 24px 32px 32px;
}

.debug-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.debug-header p {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}

.debug-form {
  margin-top: 20px;
}

.debug-form h2 {
  margin-top: 16px;
  margin-bottom: 8px;
  font-size: 18px;
  color: #b22222;
}

.form-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.form-row label {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.form-row input,
.form-row select {
  padding: 8px 10px;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 14px;
}

.form-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-primary,
.btn-secondary {
  padding: 8px 18px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: none;
}

.btn-primary {
  background-color: #dc143c;
  color: #fff;
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.success-message {
  margin-top: 16px;
  padding: 10px 12px;
  background-color: #d4edda;
  color: #155724;
  border-radius: 4px;
  font-size: 14px;
}

.error-message {
  margin-top: 16px;
  padding: 10px 12px;
  background-color: #f8d7da;
  color: #721c24;
  border-radius: 4px;
  font-size: 14px;
}

.tracking {
  font-family: monospace;
  font-weight: 600;
  color: #dc143c;
}
</style>


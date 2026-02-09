<template>
  <div class="user-messages-container">
    <div class="page-header">
      <h1>我的消息</h1>
      <div class="header-actions">
        <span class="unread-count" v-if="unreadCount > 0">
          未读：{{ unreadCount }}
        </span>
        <button 
          v-if="unreadCount > 0"
          class="mark-all-read-btn" 
          @click="handleMarkAllAsRead" 
          :disabled="loading"
        >
          一键已读
        </button>
        <button class="refresh-btn" @click="fetchMessages" :disabled="loading">
          {{ loading ? '刷新中...' : '刷新列表' }}
        </button>
      </div>
    </div>

    <div class="messages-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
        <span class="tab-count">({{ getTabCount(tab.key) }})</span>
      </button>
    </div>

    <div class="messages-list">
      <div
        v-for="message in displayedMessages"
        :key="message.id"
        :class="['message-card', { unread: message.status === '未读' }]"
        @click="handleMessageClick(message)"
      >
        <div class="message-header">
          <div class="message-title-row">
            <h3 class="message-title">{{ message.title }}</h3>
            <span v-if="message.status === '未读'" class="unread-badge">未读</span>
          </div>
          <div class="message-meta">
            <span class="message-type">{{ message.messageType }}</span>
            <span class="message-time">{{ formatDate(message.sendTime) }}</span>
          </div>
        </div>
        <div class="message-content">
          <p>{{ message.content }}</p>
        </div>
      </div>
      <div v-if="displayedMessages.length === 0 && !loading" class="empty-message">
        {{ getEmptyMessage() }}
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
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'UserMessagesView',
  setup() {
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const activeTab = ref('all')
    const messages = ref([])
    const unreadCount = ref(0)

    const tabs = [
      { key: 'all', label: '全部消息' },
      { key: 'unread', label: '未读消息' },
      { key: 'read', label: '已读消息' }
    ]

    const displayedMessages = computed(() => {
      if (activeTab.value === 'unread') {
        return messages.value.filter(msg => msg.status === '未读')
      } else if (activeTab.value === 'read') {
        return messages.value.filter(msg => msg.status === '已读')
      }
      return messages.value
    })

    const getTabCount = (key) => {
      if (key === 'unread') {
        return messages.value.filter(msg => msg.status === '未读').length
      } else if (key === 'read') {
        return messages.value.filter(msg => msg.status === '已读').length
      }
      return messages.value.length
    }

    const getEmptyMessage = () => {
      if (activeTab.value === 'unread') {
        return '暂无未读消息'
      } else if (activeTab.value === 'read') {
        return '暂无已读消息'
      }
      return '暂无消息'
    }

    const formatDate = (dateTime) => {
      if (!dateTime) return '暂无'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const fetchMessages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.id) {
          errorMessage.value = '用户未登录'
          return
        }

        const [messagesResponse, countResponse] = await Promise.all([
          api.getUserMessages(user.id),
          api.getUnreadMessageCount(user.id)
        ])

        if (messagesResponse.data.success) {
          messages.value = messagesResponse.data.data || []
        } else {
          errorMessage.value = messagesResponse.data.message || '获取消息列表失败'
        }

        if (countResponse.data.success) {
          unreadCount.value = countResponse.data.data || 0
        }
      } catch (error) {
        console.error('获取消息列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取消息列表失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    const handleMessageClick = async (message) => {
      if (message.status === '未读') {
        try {
          await api.markMessageAsRead(message.id)
          message.status = '已读'
          message.readTime = new Date().toISOString()
          unreadCount.value = Math.max(0, unreadCount.value - 1)
        } catch (error) {
          console.error('标记已读失败:', error)
        }
      }
    }

    const handleMarkAllAsRead = async () => {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        if (!user.id) {
          errorMessage.value = '用户未登录'
          return
        }

        loading.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const response = await api.markAllMessagesAsRead(user.id)
        if (response.data.success) {
          const count = response.data.data.count || 0
          successMessage.value = `已成功标记 ${count} 条消息为已读`
          unreadCount.value = 0
          await fetchMessages()
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)
        } else {
          errorMessage.value = response.data.message || '一键已读失败'
        }
      } catch (error) {
        console.error('一键已读失败:', error)
        errorMessage.value = error.response?.data?.message || '一键已读失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchMessages()
    })

    return {
      loading,
      successMessage,
      errorMessage,
      activeTab,
      messages,
      unreadCount,
      tabs,
      displayedMessages,
      getTabCount,
      getEmptyMessage,
      formatDate,
      fetchMessages,
      handleMessageClick,
      handleMarkAllAsRead
    }
  }
}
</script>

<style scoped>
.user-messages-container {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.unread-count {
  color: #DC143C;
  font-weight: 600;
  font-size: 16px;
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

.mark-all-read-btn {
  padding: 12px 24px;
  background: #28a745;
  color: white;
  border: 2px solid #28a745;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.mark-all-read-btn:hover:not(:disabled) {
  background: #218838;
  border-color: #218838;
}

.mark-all-read-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.messages-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 2px solid #e0e0e0;
}

.tab-btn {
  padding: 12px 24px;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  color: #666;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.tab-btn:hover {
  color: #DC143C;
}

.tab-btn.active {
  color: #DC143C;
  border-bottom-color: #DC143C;
  font-weight: 600;
}

.tab-count {
  margin-left: 8px;
  color: #999;
  font-weight: normal;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 0;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.message-card:hover {
  border-color: #DC143C;
  box-shadow: 0 2px 8px rgba(220, 20, 60, 0.1);
}

.message-card.unread {
  border-left: 4px solid #DC143C;
  background: #fff9f9;
}

.message-header {
  margin-bottom: 12px;
}

.message-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  flex: 1;
}

.unread-badge {
  padding: 4px 12px;
  background: #DC143C;
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 0;
}

.message-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

.message-type {
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 0;
  color: #555;
}

.message-time {
  color: #999;
}

.message-content {
  color: #555;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.success-message {
  margin-top: 20px;
  padding: 12px;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
  border-radius: 0;
  text-align: center;
}

.error-message {
  margin-top: 20px;
  padding: 12px;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  border-radius: 0;
  text-align: center;
}
</style>

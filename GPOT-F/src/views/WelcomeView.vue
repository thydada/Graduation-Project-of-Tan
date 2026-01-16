<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <div class="welcome-header">
        <h1 class="welcome-title">欢迎使用 GPOT 快递管理系统</h1>
        <div class="welcome-icon">🎉</div>
      </div>

      <div class="welcome-content">
        <p class="welcome-message">
          您已成功登录系统！这里是您的个人主页。
        </p>

        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">📦</div>
            <h3>包裹管理</h3>
            <p>查看和管理您的快递包裹</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">📍</div>
            <h3>地址管理</h3>
            <p>管理收货和发货地址</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">📊</div>
            <h3>订单查询</h3>
            <p>查询订单状态和历史记录</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">🔔</div>
            <h3>消息通知</h3>
            <p>接收系统消息和提醒</p>
          </div>
        </div>
      </div>

      <div class="welcome-footer">
        <p class="coming-soon-text">
          更多功能正在开发中，敬请期待...
        </p>
        <button @click="handleLogout" class="logout-btn">
          退出登录
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'WelcomeView',
  setup() {
    const router = useRouter()

    const handleLogout = () => {
      // 简单地返回登录页面
      // 在实际应用中，这里应该清除用户会话/令牌
      router.push('/')
    }

    return {
      handleLogout
    }
  }
}
</script>

<style scoped>
.welcome-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 800px;
  text-align: center;
}

.welcome-header {
  margin-bottom: 30px;
}

.welcome-title {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.3;
}

.welcome-icon {
  font-size: 48px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.welcome-content {
  margin-bottom: 40px;
}

.welcome-message {
  color: #666;
  font-size: 18px;
  margin-bottom: 40px;
  line-height: 1.6;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.feature-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #e9ecef;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 32px;
  margin-bottom: 15px;
}

.feature-card h3 {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
}

.feature-card p {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.welcome-footer {
  border-top: 1px solid #e9ecef;
  padding-top: 30px;
}

.coming-soon-text {
  color: #888;
  font-size: 14px;
  margin-bottom: 20px;
  font-style: italic;
}

.logout-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .welcome-card {
    padding: 30px 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .feature-card {
    padding: 20px;
  }
}
</style>
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api', // 通过vite代理到后端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加认证token等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// API方法
const apiService = {
  // 用户登录
  async login(loginData) {
    return api.post('/login', loginData)
  },

  // 用户注册
  async register(registerData) {
    return api.post('/register', registerData)
  }
}

export default apiService
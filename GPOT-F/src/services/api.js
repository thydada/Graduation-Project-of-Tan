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
  },

  // 寄件
  async sendPackage(packageData) {
    return api.post('/send-package', packageData)
  },

  // 获取用户包裹列表
  async getUserPackages(userId) {
    return api.get(`/packages/user/${userId}`)
  },

  // 根据快递单号查询包裹
  async getPackageByTrackingNumber(trackingNumber) {
    return api.get(`/packages/tracking/${trackingNumber}`)
  },

  // 查询待取件的临时快递
  async getPendingPackages() {
    return api.get('/packages/temp/pending')
  },

  // 审核快递取件情况
  async verifyPackage(id, status) {
    return api.put(`/packages/temp/${id}/verify`, { status })
  },

  // 查询待核验的临时快递
  async getVerificationPendingPackages() {
    return api.get('/packages/temp/verification-pending')
  },

  // 核验快递
  async verificationPackage(id, status, employeeId = 1, warehouseId = 1, shelfId = null, shelfLayer = null) {
    const requestData = {
      status,
      employeeId,
      warehouseId
    }
    if (shelfId !== null) {
      requestData.shelfId = shelfId
    }
    if (shelfLayer !== null) {
      requestData.shelfLayer = shelfLayer
    }
    return api.put(`/packages/temp/${id}/verification`, requestData)
  },

  // 报告异常件
  async reportException(tempPackageId, exceptionType, exceptionReason, employeeId = 1, source) {
    return api.post('/packages/temp/report-exception', {
      tempPackageId,
      exceptionType,
      exceptionReason,
      employeeId,
      source
    })
  },

  // 获取所有异常件列表
  async getAllExceptionPackages() {
    return api.get('/exception-packages')
  },

  // 获取用户的所有包裹信息（临时包裹、正式包裹、异常包裹）
  async getUserAllPackages(userId) {
    return api.get(`/packages/user/${userId}/all`)
  },

  // 获取已入库的包裹列表（员工B出库使用）
  async getInStockPackages() {
    return api.get('/packages/in-stock')
  },

  // 出库操作（员工B）
  async outboundPackage(packageId, employeeId) {
    return api.post(`/packages/${packageId}/outbound`, { employeeId })
  },

  // 获取分配给指定员工的运输中包裹列表（员工A使用）
  async getTransportingPackages(employeeId) {
    return api.get(`/packages/transporting/${employeeId}`)
  },

  // 送达操作（员工A）
  async deliverPackage(packageId, employeeId) {
    return api.post(`/packages/${packageId}/deliver`, { employeeId })
  }
}

export default apiService
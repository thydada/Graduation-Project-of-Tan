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

  // 获取用户包裹列表
  async getUserPackages(userId) {
    return api.get(`/packages/user/${userId}`)
  },

  // 根据快递单号查询包裹
  async getPackageByTrackingNumber(trackingNumber) {
    return api.get(`/packages/tracking/${trackingNumber}`)
  },

  // 根据收件人电话查询已入库的包裹列表（终端机查询使用）
  async getPackagesByReceiverPhone(receiverPhone) {
    return api.get(`/packages/phone/${receiverPhone}`)
  },

  // 用户取件操作（终端机出库使用）
  async userPickupPackage(trackingNumber, pickupCode) {
    return api.post(`/packages/pickup/${trackingNumber}`, { pickupCode })
  },


  // 获取所有异常件列表
  async getAllExceptionPackages() {
    return api.get('/exception-packages')
  },

  // 获取用户的所有包裹信息（正式包裹、异常包裹）
  async getUserAllPackages(userId) {
    return api.get(`/packages/user/${userId}/all`)
  },

  // 分页获取用户的所有包裹信息，支持查询
  async getUserAllPackagesWithPagination(userId, keyword = '', page = 0, size = 10, type = 'all') {
    const params = new URLSearchParams()
    if (keyword) params.append('keyword', keyword)
    params.append('page', page)
    params.append('size', size)
    params.append('type', type)
    return api.get(`/packages/user/${userId}/all/paged?${params.toString()}`)
  },

  // 获取已入库的包裹列表（员工B出库使用）
  async getInStockPackages() {
    return api.get('/packages/in-stock')
  },

  // 获取所有包裹列表（员工B查看）
  async getAllPackages() {
    return api.get('/packages/all')
  },

  // 获取待入库的正式包裹列表（package 表）
  async getPendingInboundPackages() {
    return api.get('/packages/pending-inbound')
  },

  // Debug：直接创建正式包裹
  async debugCreatePackage(data) {
    return api.post('/debug/packages', data)
  },

  // 出库操作（员工B）
  async outboundPackage(packageId, employeeId) {
    return api.post(`/packages/${packageId}/outbound`, { employeeId })
  },

  // 获取分配给指定员工的运输中包裹列表（员工A使用）
  // 【功能已禁用】该API已被禁用，但为了保持系统完整性未被删除，请勿依赖此功能
  async getTransportingPackages(employeeId) {
    return api.get(`/packages/transporting/${employeeId}`)
  },

  // 送达操作（员工A）
  // 【功能已禁用】该API已被禁用，但为了保持系统完整性未被删除，请勿依赖此功能
  async deliverPackage(packageId, employeeId) {
    return api.post(`/packages/${packageId}/deliver`, { employeeId })
  },

  // 正式包裹入库操作（从待入库 -> 已入库）
  async inboundFormalPackage(packageId, employeeId, warehouseId, shelfId = null, shelfLayer = null) {
    const requestData = {
      employeeId,
      warehouseId
    }
    if (shelfId !== null) {
      requestData.shelfId = shelfId
    }
    if (shelfLayer !== null) {
      requestData.shelfLayer = shelfLayer
    }
    return api.put(`/packages/${packageId}/inbound`, requestData)
  },

  // 报告正式包裹异常件
  async reportFormalPackageException(packageId, exceptionType, exceptionReason, employeeId = 1, source = 'inbound') {
    return api.post(`/packages/${packageId}/report-exception`, {
      exceptionType,
      exceptionReason,
      employeeId,
      source
    })
  },

  // 获取管理员统计数据
  async getAdminStatistics() {
    return api.get('/admin/statistics')
  },

  // 获取近几日入库统计
  async getDailyEntryStatistics(days = 7) {
    return api.get(`/admin/daily-entry-statistics?days=${days}`)
  },

  // 获取近几日出库统计
  async getDailyOutboundStatistics(days = 3) {
    return api.get(`/admin/daily-outbound-statistics?days=${days}`)
  },

  // 导出运维监控数据到根目录
  async exportAdminData(content) {
    return api.post('/admin/export-data', { content })
  },

  // 获取用户消息列表
  async getUserMessages(userId) {
    return api.get(`/messages/user/${userId}`)
  },

  // 获取用户未读消息列表
  async getUnreadMessages(userId) {
    return api.get(`/messages/user/${userId}/unread`)
  },

  // 获取用户未读消息数量
  async getUnreadMessageCount(userId) {
    return api.get(`/messages/user/${userId}/unread-count`)
  },

  // 标记消息为已读
  async markMessageAsRead(messageId) {
    return api.put(`/messages/${messageId}/read`)
  },

  // 一键标记所有消息为已读
  async markAllMessagesAsRead(userId) {
    return api.put(`/messages/user/${userId}/read-all`)
  },

  // 发送消息给所有用户
  async sendMessageToAllUsers(data) {
    return api.post('/messages/send-to-all', data)
  }
}

export default apiService
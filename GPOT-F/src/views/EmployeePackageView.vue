<template>
  <div class="package-container">
    <div class="page-header">
      <h1>快递入库</h1>
      <div class="header-actions">
        <div class="sub-tabs">
        </div>
      </div>
    </div>

    <!-- 扫码入库子页面 -->
    <div v-if="activeTab === 'scan'" class="scan-section">
      <div class="scan-card">
        <div class="scan-card-header">
          
          <h2>扫码入库</h2>
        </div>
        <p class="scan-desc">
          在此输入快递单号和货架信息，系统将根据快递单号在正式表中查找
          <span class="highlight-text">状态为"待入库"</span> 的包裹并完成入库。
        </p>

        <div class="scan-form">
          <div class="form-row">
            <label class="form-label">

              快递单号 <span class="required">*</span>
            </label>
            <input
              v-model="scanTrackingNumber"
              type="text"
              class="form-input"
              placeholder="请输入/粘贴快递单号（模拟扫描条形码）"
              @keyup.enter="handleScanEnter"
              ref="scanTrackingInput"
            />
          </div>

          <div class="form-row">
            <label class="form-label">

              货架号 <span class="required">*</span>
            </label>
            <select v-model.number="scanShelfId" class="form-select">
              <option :value="null">请选择货架</option>
              <option :value="1">货架1（普通）</option>
              <option :value="2">货架2（普通）</option>
              <option :value="3">货架3（普通）</option>
              <option :value="4">货架4（大货架）</option>
            </select>
          </div>

          <div class="form-row">
            <label class="form-label">

              层数 <span class="required">*</span>
            </label>
            <select v-model.number="scanShelfLayer" class="form-select" :disabled="!scanShelfId">
              <option :value="null">请选择层数</option>
              <option v-for="layer in 4" :key="layer" :value="layer">第{{ layer }}层</option>
            </select>
          </div>

          <div class="scan-actions">
            <button class="btn btn-scan" @click="handleScanInbound" :disabled="scanLoading">
              <span v-if="!scanLoading" class="btn-text"> 扫码入库完成</span>
              <span v-else class="btn-text">入库中...</span>
            </button>
          </div>

          <div v-if="scanSuccessMessage" class="scan-success-message">
            <span class="success-icon">✓</span>
            {{ scanSuccessMessage }}
          </div>
          <div v-if="scanErrorMessage" class="scan-error-message">
            <span class="error-icon">✗</span>
            {{ scanErrorMessage }}
          </div>
        </div>
      </div>
    </div>

    <!-- 异常件登记子页面 -->
    <div v-if="activeTab === 'exception'" class="scan-section">
      <div class="scan-card">
        <div class="scan-card-header">
          <h2>异常件登记</h2>
        </div>
        <p class="scan-desc">
          在此输入快递单号，系统将根据快递单号在正式表中查找
          <span class="highlight-text">状态为"待入库"</span> 的包裹并标记为异常。
        </p>

        <div class="scan-form">
          <div class="form-row">
            <label class="form-label">
              快递单号 <span class="required">*</span>
            </label>
            <input
              v-model="exceptionTrackingNumber"
              type="text"
              class="form-input"
              placeholder="请输入/粘贴快递单号（模拟扫描条形码）"
            />
          </div>

          <div class="form-row">
            <label class="form-label">
              异常类型 <span class="required">*</span>
            </label>
            <select v-model="exceptionType" class="form-select">
              <option :value="null">请选择异常类型</option>
              <option value="包裹破损">包裹破损</option>
              <option value="收件人信息错误">收件人信息错误</option>
              <option value="包裹信息不符">包裹信息不符</option>
              <option value="其他">其他</option>
            </select>
          </div>

          <div class="form-row">
            <label class="form-label">
              异常原因
            </label>
            <textarea
              v-model="exceptionReason"
              class="form-input"
              placeholder="请输入异常原因（可选）"
              rows="3"
            ></textarea>
          </div>

          <div class="scan-actions">
            <button class="btn btn-scan" @click="handleExceptionReport" :disabled="exceptionLoading">
              <span v-if="!exceptionLoading" class="btn-text"> 异常件登记完成</span>
              <span v-else class="btn-text">登记中...</span>
            </button>
          </div>

          <div v-if="exceptionSuccessMessage" class="scan-success-message">
            <span class="success-icon">✓</span>
            {{ exceptionSuccessMessage }}
          </div>
          <div v-if="exceptionErrorMessage" class="scan-error-message">
            <span class="error-icon">✗</span>
            {{ exceptionErrorMessage }}
          </div>
        </div>
      </div>
    </div>

    <!-- 手动入库子页面（原列表页面） -->
    <div v-if="activeTab === 'manual'">
      <div class="manual-header">
        <div class="shelf-selector">
          <label class="selector-label">货架：</label>
          <select v-model.number="selectedShelfId" class="shelf-select" @change="saveShelfSelection">
            <option :value="null">请选择货架</option>
            <option :value="1">货架1（普通）</option>
            <option :value="2">货架2（普通）</option>
            <option :value="3">货架3（普通）</option>
            <option :value="4">货架4（大货架）</option>
          </select>
          <label class="selector-label">层数：</label>
          <select v-model.number="selectedShelfLayer" class="shelf-select" :disabled="!selectedShelfId" @change="saveShelfSelection">
            <option :value="null">请选择层数</option>
            <option v-for="layer in 4" :key="layer" :value="layer">第{{ layer }}层</option>
          </select>
          <span v-if="selectedShelfId && selectedShelfLayer" class="selection-info">
            当前选择：货架{{ selectedShelfId }} 第{{ selectedShelfLayer }}层
          </span>
        </div>
        <button class="refresh-btn" @click="fetchPackages" :disabled="loading">
          {{ loading ? '刷新中...' : '刷新列表' }}
        </button>
      </div>

    <!-- 快递列表表格 -->
    <div class="table-container" v-if="activeTab === 'manual'">
      <table class="package-table">
        <thead>
          <tr>
            <th v-for="header in tableHeaders" :key="header.key">{{ header.label }}</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pkg in packages" :key="pkg.id">
            <td class="tracking-number">{{ pkg.trackingNumber }}</td>
            <td>{{ pkg.senderName }}</td>
            <td>{{ pkg.senderPhone }}</td>
            <td class="address">{{ pkg.senderAddress }}</td>
            <td>{{ pkg.packageType }}</td>
            <td>{{ pkg.weight }}</td>
            <td>{{ formatDate(pkg.entryTime) }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(pkg)]">
                {{ getStatusText(pkg) }}
              </span>
            </td>
            <td class="actions">
              <button
                class="btn btn-success"
                @click="handleSuccess(pkg.id)"
                :disabled="processingId === pkg.id"
              >
                入库完成
              </button>
            </td>
          </tr>
          <tr v-if="packages.length === 0 && !loading">
            <td :colspan="tableHeaders.length + 1" class="empty-message">暂无待入库的快递</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 成功提示 -->
    <div v-if="successMessage" class="success-message" v-show="activeTab === 'manual'">
      {{ successMessage }}
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message" v-show="activeTab === 'manual'">
      {{ errorMessage }}
    </div>

    <!-- 货架匹配提醒弹窗 -->
    <div v-if="showShelfWarningModal" class="modal-overlay" @click.self="cancelShelfWarning">
      <div class="modal-content shelf-warning-modal">
        <div class="modal-header">
          <h3>
            <span class="warning-icon">⚠️</span>
            货架位置提醒
          </h3>
          <button class="close-btn" @click="cancelShelfWarning">&times;</button>
        </div>
        <div class="modal-body">
          <p class="warning-message">{{ shelfWarningMessage }}</p>
          <div v-if="shelfWarningData && shelfWarningData.pkg" class="package-info">
            <div class="info-row">
              <span class="info-label">快递单号：</span>
              <span class="info-value tracking-number">{{ shelfWarningData.pkg.trackingNumber }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">包裹类型：</span>
              <span class="info-value">{{ shelfWarningData.pkg.packageType }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">重量：</span>
              <span class="info-value">{{ shelfWarningData.pkg.weight }} kg</span>
            </div>
            <div class="info-row">
              <span class="info-label">尺寸：</span>
              <span class="info-value">{{ shelfWarningData.pkg.size || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">当前货架：</span>
              <span class="info-value">{{ activeTab === 'scan' ? (scanShelfId === 4 ? '货架4（大货架）' : `货架${scanShelfId}（普通）`) : (selectedShelfId === 4 ? '货架4（大货架）' : `货架${selectedShelfId}（普通）`) }}</span>
            </div>
          </div>
          <p class="confirm-text">是否使用推荐货架位置继续入库？</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="cancelShelfWarning">取消</button>
          <button class="btn-submit" @click="confirmRecommendedShelf">
            确认使用推荐货架
          </button>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import api from '../services/api'

export default {
  name: 'EmployeePackageView',
  setup() {
    const packages = ref([])
    const loading = ref(false)
    const processingId = ref(null)
    const successMessage = ref('')
    const errorMessage = ref('')

    // 货架和层数选择
    const selectedShelfId = ref(null)
    const selectedShelfLayer = ref(null)

    // 货架匹配提醒弹窗相关
    const showShelfWarningModal = ref(false)
    const shelfWarningMessage = ref('')
    const shelfWarningData = ref(null) // 待入库的包裹信息

    // 当前入库模式：scan 扫码入库，manual 手动入库，exception 异常件登记（由路由决定）
    const route = useRoute()
    const activeTab = ref(
      route.name === 'EmployeePackageManual' ? 'manual' :
      route.name === 'EmployeePackageException' ? 'exception' : 'scan'
    )

    // 监听路由变化，实时切换 tab
    watch(() => route.name, (newRouteName) => {
      if (newRouteName === 'EmployeePackageManual') {
        activeTab.value = 'manual'
      } else if (newRouteName === 'EmployeePackageScan') {
        activeTab.value = 'scan'
      } else if (newRouteName === 'EmployeePackageException') {
        activeTab.value = 'exception'
      }
    })

    // 扫码入库相关
    const scanTrackingNumber = ref('')
    const scanShelfId = ref(null)
    const scanShelfLayer = ref(null)
    const scanLoading = ref(false)
    const scanSuccessMessage = ref('')
    const scanErrorMessage = ref('')
    const scanTrackingInput = ref(null)

    // 异常件登记相关
    const exceptionTrackingNumber = ref('')
    const exceptionType = ref(null)
    const exceptionReason = ref('')
    const exceptionLoading = ref(false)
    const exceptionSuccessMessage = ref('')
    const exceptionErrorMessage = ref('')

    // 获取当前登录员工信息
    const getCurrentEmployeeId = () => {
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
      return userInfo.id || 1
    }

    /**
     * 判断包裹是否为大件货
     * @param pkg 包裹对象，包含size和weight属性
     * @returns true-大件货，false-小件货
     */
    const isLargePackage = (pkg) => {
      // 解析尺寸
      if (pkg.size && pkg.size.trim()) {
        try {
          const dimensions = pkg.size.split('x')
          if (dimensions.length === 3) {
            const length = parseFloat(dimensions[0].trim())
            const width = parseFloat(dimensions[1].trim())
            const height = parseFloat(dimensions[2].trim())
            const volume = length * width * height // 体积（cm³）

            // 如果体积大于50000 cm³（约0.05立方米）或重量大于5kg，使用大货架
            if (volume > 50000) {
              return true
            }
          }
        } catch (e) {
          // 解析失败，继续用重量判断
        }
      }

      // 根据重量判断（如果重量大于5kg）
      if (pkg.weight) {
        const weight = parseFloat(pkg.weight)
        if (!isNaN(weight) && weight > 5) {
          return true
        }
      }

      return false
    }

    /**
     * 检查包裹与货架是否匹配
     * @param pkg 包裹对象
     * @param shelfId 货架ID
     * @returns { matched: boolean, suggestion: string, recommendedShelfId: number }
     */
    const checkShelfMatch = (pkg, shelfId) => {
      const isLarge = isLargePackage(pkg)
      const isLargeShelf = shelfId === 4

      // 小件货放入大货架 - 提示放在小货架
      if (!isLarge && isLargeShelf) {
        return {
          matched: false,
          suggestion: '该快递体积较小，建议放入普通货架',
          recommendedShelfId: null, // 让用户重新选择
          recommendedShelfType: 'normal'
        }
      }

      // 大件货放入小货架 - 提示放在大货架
      if (isLarge && !isLargeShelf) {
        return {
          matched: false,
          suggestion: '该快递体积较大，建议放入大货架（货架4）',
          recommendedShelfId: 4,
          recommendedShelfType: 'large'
        }
      }

      return {
        matched: true,
        suggestion: '',
        recommendedShelfId: null,
        recommendedShelfType: null
      }
    }

    /**
     * 打开货架不匹配提醒弹窗
     */
    const openShelfWarningModal = (message, pkg, recommendedShelfId, recommendedShelfType) => {
      shelfWarningMessage.value = message
      shelfWarningData.value = {
        pkg: pkg,
        recommendedShelfId: recommendedShelfId,
        recommendedShelfType: recommendedShelfType
      }
      showShelfWarningModal.value = true
    }

    /**
     * 确认使用推荐货架
     */
    const confirmRecommendedShelf = () => {
      if (shelfWarningData.value) {
        const { pkg, recommendedShelfId, recommendedShelfType } = shelfWarningData.value

        // 自动设置货架
        if (recommendedShelfId) {
          selectedShelfId.value = recommendedShelfId
        }

        // 重新触发入库操作
        executeInbound(pkg.id)
      }
      showShelfWarningModal.value = false
    }

    /**
     * 取消提醒，继续使用当前选择
     */
    const cancelShelfWarning = () => {
      if (shelfWarningData.value) {
        const { pkg } = shelfWarningData.value
        // 继续执行入库，不改变货架
        executeInbound(pkg.id)
      }
      showShelfWarningModal.value = false
    }

    /**
     * 执行入库操作（内部方法）
     */
    const executeInbound = async (pkgId) => {
      processingId.value = pkgId
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const employeeId = getCurrentEmployeeId()

        if (!selectedShelfId.value || !selectedShelfLayer.value) {
          errorMessage.value = '请先选择货架和层数'
          processingId.value = null
          return
        }

        const response = await api.inboundFormalPackage(
          pkgId,
          employeeId,
          1, // warehouseId 默认1
          selectedShelfId.value,
          selectedShelfLayer.value
        )

        if (response.data.success) {
          packages.value = packages.value.filter(p => p.id !== pkgId)
          successMessage.value = response.data.message || '入库成功'
        } else {
          errorMessage.value = response.data.message || '操作失败'
        }
      } catch (error) {
        console.error('操作失败:', error)
        errorMessage.value = error.response?.data?.message || '操作失败，请稍后重试'
      } finally {
        processingId.value = null
      }
    }

    // 表格表头
    const tableHeaders = computed(() => {
      return [
        { key: 'trackingNumber', label: '快递单号' },
        { key: 'senderName', label: '寄件人' },
        { key: 'senderPhone', label: '寄件人电话' },
        { key: 'senderAddress', label: '寄件地址' },
        { key: 'packageType', label: '包裹类型' },
        { key: 'weight', label: '重量(kg)' },
        { key: 'entryTime', label: '入库时间' },
        { key: 'status', label: '入库状态' }
      ]
    })

    // 获取快递列表
    const fetchPackages = async () => {
      loading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        // 查询 package 表中所有状态为“待入库”的正式包裹
        const response = await api.getPendingInboundPackages()

        if (response.data.success) {
          packages.value = response.data.data
        } else {
          errorMessage.value = response.data.message || '获取快递列表失败'
        }
      } catch (error) {
        console.error('获取快递列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取快递列表失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    // 扫码入库逻辑：回车快捷入库
    const handleScanEnter = async () => {
      if (scanLoading.value) return
      await handleScanInbound()
      // 入库成功后，自动聚焦回输入框，方便连续扫描
      if (scanSuccessMessage.value && scanTrackingInput.value) {
        scanTrackingInput.value.focus()
      }
    }

    // 扫码入库逻辑：根据快递单号查询 package，再调用正式入库接口
    const handleScanInbound = async () => {
      scanLoading.value = true
      scanSuccessMessage.value = ''
      scanErrorMessage.value = ''

      try {
        if (!scanTrackingNumber.value) {
          scanErrorMessage.value = '请先输入快递单号'
          scanLoading.value = false
          return
        }
        if (!scanShelfId.value || !scanShelfLayer.value) {
          scanErrorMessage.value = '请先选择货架和层数（模拟扫描货架条形码）'
          scanLoading.value = false
          return
        }

        // 1. 通过快递单号查询包裹
        const pkgResp = await api.getPackageByTrackingNumber(scanTrackingNumber.value)
        if (!pkgResp.data || !pkgResp.data.success || !pkgResp.data.data) {
          scanErrorMessage.value = pkgResp.data?.message || '未找到该快递单号对应的包裹'
          scanLoading.value = false
          return
        }

        const pkg = pkgResp.data.data
        if (pkg.status !== '待入库') {
          scanErrorMessage.value = `该包裹当前状态为「${pkg.status || '未知'}」，无法执行入库（仅支持待入库）`
          scanLoading.value = false
          return
        }

        // 检查包裹与货架是否匹配
        const matchResult = checkShelfMatch(pkg, scanShelfId.value)
        if (!matchResult.matched) {
          // 显示提醒弹窗
          openShelfWarningModal(matchResult.suggestion, pkg, matchResult.recommendedShelfId, matchResult.recommendedShelfType)
          scanLoading.value = false
          return
        }

        const employeeId = getCurrentEmployeeId()

        // 2. 调用正式入库接口
        const inboundResp = await api.inboundFormalPackage(
          pkg.id,
          employeeId,
          1, // 默认仓库1
          scanShelfId.value,
          scanShelfLayer.value
        )

        if (inboundResp.data && inboundResp.data.success) {
          scanSuccessMessage.value = inboundResp.data.message || '扫码入库成功'
          // 清空表单
          scanTrackingNumber.value = ''
          // 如果手动列表中也有这条包裹，则同步移除
          packages.value = packages.value.filter(p => p.id !== pkg.id)
        } else {
          scanErrorMessage.value = inboundResp.data?.message || '入库失败'
        }
      } catch (e) {
        console.error('handleScanInbound error', e)
        scanErrorMessage.value = e.response?.data?.message || e.message || '入库失败，请稍后重试'
      } finally {
        scanLoading.value = false
      }
    }

    // 保存货架选择到localStorage
    const saveShelfSelection = () => {
      if (selectedShelfId.value && selectedShelfLayer.value) {
        localStorage.setItem('selectedShelfId', selectedShelfId.value.toString())
        localStorage.setItem('selectedShelfLayer', selectedShelfLayer.value.toString())
      }
    }

    // 从localStorage加载货架选择
    const loadShelfSelection = () => {
      const savedShelfId = localStorage.getItem('selectedShelfId')
      const savedShelfLayer = localStorage.getItem('selectedShelfLayer')
      if (savedShelfId) {
        selectedShelfId.value = parseInt(savedShelfId)
      }
      if (savedShelfLayer) {
        selectedShelfLayer.value = parseInt(savedShelfLayer)
      }
    }

    // 处理成功操作
    const handleSuccess = async (pkgId) => {
      processingId.value = pkgId
      errorMessage.value = ''
      successMessage.value = ''

      try {
        const employeeId = getCurrentEmployeeId()

        if (!selectedShelfId.value || !selectedShelfLayer.value) {
          errorMessage.value = '请先选择货架和层数'
          processingId.value = null
          return
        }

        // 查找待入库的包裹信息
        const pkg = packages.value.find(p => p.id === pkgId)
        if (!pkg) {
          errorMessage.value = '未找到该包裹信息'
          processingId.value = null
          return
        }

        // 检查包裹与货架是否匹配
        const matchResult = checkShelfMatch(pkg, selectedShelfId.value)
        if (!matchResult.matched) {
          // 打开提醒弹窗
          openShelfWarningModal(matchResult.suggestion, pkg, matchResult.recommendedShelfId, matchResult.recommendedShelfType)
          processingId.value = null
          return
        }

        // 匹配，执行入库
        const response = await api.inboundFormalPackage(
          pkgId,
          employeeId,
          1, // warehouseId 默认1
          selectedShelfId.value,
          selectedShelfLayer.value
        )

        if (response.data.success) {
          packages.value = packages.value.filter(p => p.id !== pkgId)
          successMessage.value = response.data.message || '入库成功'
        } else {
          errorMessage.value = response.data.message || '操作失败'
        }
      } catch (error) {
        console.error('操作失败:', error)
        errorMessage.value = error.response?.data?.message || '操作失败，请稍后重试'
      } finally {
        processingId.value = null
      }
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    // 获取状态文本
    const getStatusText = (pkg) => {
      // 直接使用正式包裹的 status 字段
      return pkg.status || '未知'
    }

    // 获取状态样式类
    const getStatusClass = (pkg) => {
      const status = pkg.status || ''
      if (status === '待入库') return 'status-pending'
      if (status === '已入库') return 'status-success'
      if (status.includes('异常')) return 'status-error'
      return ''
    }

    // 异常件登记逻辑：根据快递单号查询 package，再调用异常登记接口
    const handleExceptionReport = async () => {
      exceptionLoading.value = true
      exceptionSuccessMessage.value = ''
      exceptionErrorMessage.value = ''

      try {
        if (!exceptionTrackingNumber.value) {
          exceptionErrorMessage.value = '请先输入快递单号'
          exceptionLoading.value = false
          return
        }
        if (!exceptionType.value) {
          exceptionErrorMessage.value = '请先选择异常类型'
          exceptionLoading.value = false
          return
        }

        // 1. 通过快递单号查询包裹
        const pkgResp = await api.getPackageByTrackingNumber(exceptionTrackingNumber.value)
        if (!pkgResp.data || !pkgResp.data.success || !pkgResp.data.data) {
          exceptionErrorMessage.value = pkgResp.data?.message || '未找到该快递单号对应的包裹'
          exceptionLoading.value = false
          return
        }

        const pkg = pkgResp.data.data
        if (pkg.status !== '待入库') {
          exceptionErrorMessage.value = `该包裹当前状态为「${pkg.status || '未知'}」，无法执行异常登记（仅支持待入库）`
          exceptionLoading.value = false
          return
        }

        const employeeId = getCurrentEmployeeId()

        // 2. 调用异常登记接口
        const exceptionResp = await api.reportFormalPackageException(
          pkg.id,
          exceptionType.value,
          exceptionReason.value,
          employeeId,
          'inbound'
        )

        if (exceptionResp.data && exceptionResp.data.success) {
          exceptionSuccessMessage.value = exceptionResp.data.message || '异常件登记成功'
          // 清空表单
          exceptionTrackingNumber.value = ''
          exceptionType.value = null
          exceptionReason.value = ''
          // 如果手动列表中也有这条包裹，则同步移除
          packages.value = packages.value.filter(p => p.id !== pkg.id)
        } else {
          exceptionErrorMessage.value = exceptionResp.data?.message || '异常件登记失败'
        }
      } catch (e) {
        console.error('handleExceptionReport error', e)
        exceptionErrorMessage.value = e.response?.data?.message || e.message || '异常件登记失败，请稍后重试'
      } finally {
        exceptionLoading.value = false
      }
    }

    // 页面加载时获取数据
    onMounted(() => {
      loadShelfSelection()
      fetchPackages()
    })

    return {
      packages,
      loading,
      processingId,
      successMessage,
      errorMessage,
      tableHeaders,
      fetchPackages,
      handleSuccess,
      formatDate,
      getStatusText,
      getStatusClass,
      activeTab,
      // 扫码入库相关
      scanTrackingNumber,
      scanShelfId,
      scanShelfLayer,
      scanLoading,
      scanSuccessMessage,
      scanErrorMessage,
      handleScanInbound,
      handleScanEnter,
      scanTrackingInput,
      // 货架选择相关
      selectedShelfId,
      selectedShelfLayer,
      saveShelfSelection,
      // 异常件登记相关
      exceptionTrackingNumber,
      exceptionType,
      exceptionReason,
      exceptionLoading,
      exceptionSuccessMessage,
      exceptionErrorMessage,
      handleExceptionReport,
      // 货架匹配提醒弹窗相关
      showShelfWarningModal,
      shelfWarningMessage,
      shelfWarningData,
      confirmRecommendedShelf,
      cancelShelfWarning
    }
  }
}
</script>

<style scoped>
.package-container {
  min-height: 100%;
}

/* 扫码入库样式 */
.scan-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.scan-card {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
  overflow: hidden;
}

.scan-card-header {
  background: #DC143C;
  color: white;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.scan-icon {
  font-size: 28px;
}

.scan-card-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.scan-desc {
  padding: 20px 24px;
  margin: 0;
  background: #fff5f5;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  border-bottom: 1px solid #ffebeb;
}

.highlight-text {
  color: #DC143C;
  font-weight: 700;
}

.scan-form {
  padding: 24px;
}

.form-row {
  margin-bottom: 20px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.label-icon {
  font-size: 16px;
}

.required {
  color: #DC143C;
}

.form-input,
.form-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 0;
  font-size: 15px;
  color: #333;
  background: white;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 3px rgba(220, 20, 60, 0.1);
}

.form-input::placeholder {
  color: #aaa;
}

.form-select:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.scan-actions {
  margin-top: 28px;
  margin-bottom: 20px;
}

.btn-scan {
  width: 100%;
  padding: 14px 24px;
  background: #DC143C;
  color: white;
  border: 2px solid #DC143C;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-scan:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.btn-scan:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-text {
  display: flex;
  align-items: center;
  gap: 6px;
}

.scan-success-message {
  margin-top: 16px;
  padding: 14px 16px;
  background: #d4edda;
  color: #155724;
  border-radius: 0;
  text-align: left;
  border: 2px solid #c3e6cb;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.success-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background: #28a745;
  color: white;
  border-radius: 50%;
  font-size: 14px;
  font-weight: bold;
}

.scan-error-message {
  margin-top: 16px;
  padding: 14px 16px;
  background: #fee;
  color: #c33;
  border-radius: 0;
  text-align: left;
  border: 2px solid #fcc;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background: #dc3545;
  color: white;
  border-radius: 50%;
  font-size: 14px;
  font-weight: bold;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.shelf-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: #f8f8f8;
  border: 2px solid #DC143C;
  border-radius: 4px;
}

.selector-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.shelf-select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  min-width: 120px;
}

.shelf-select:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.2);
}

.shelf-select:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.selection-info {
  font-size: 14px;
  color: #DC143C;
  font-weight: 600;
  white-space: nowrap;
}

.page-header h1 {
  color: #333;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
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

.table-container {
  background: white;
  border-radius: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.package-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.package-table th {
  background: #DC143C;
  color: white;
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  white-space: nowrap;
}

.package-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee;
  color: #333;
}

.package-table tr:hover {
  background: #fff5f5;
}

.package-table tr:last-child td {
  border-bottom: none;
}

.tracking-number {
  font-family: monospace;
  font-weight: 600;
  color: #DC143C;
}

.address {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-success {
  background: #d4edda;
  color: #155724;
}

.status-error {
  background: #f8d7da;
  color: #721c24;
}

.actions {
  white-space: nowrap;
}

.btn {
  padding: 8px 16px;
  margin-right: 8px;
  border: 2px solid;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.btn:last-child {
  margin-right: 0;
}

.btn-success {
  background: #28a745;
  color: white;
  border-color: #28a745;
}

.btn-success:hover:not(:disabled) {
  background: #218838;
  border-color: #218838;
}

.btn-error {
  background: #dc3545;
  color: white;
  border-color: #dc3545;
}

.btn-error:hover:not(:disabled) {
  background: #c82333;
  border-color: #c82333;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.empty-message {
  text-align: center;
  padding: 48px !important;
  color: #999;
  font-size: 16px;
}

.success-message {
  margin-top: 20px;
  padding: 14px;
  background: #d4edda;
  color: #155724;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #c3e6cb;
}

.error-message {
  margin-top: 20px;
  padding: 14px;
  background: #fee;
  color: #c33;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #fcc;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 480px;
  max-width: 90%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #333;
  font-weight: 500;
}

.tracking-info {
  font-family: monospace;
  color: #DC143C;
  font-weight: 600;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #DC143C;
  box-shadow: 0 0 0 2px rgba(220, 20, 60, 0.2);
}

textarea.form-control {
  resize: vertical;
  min-height: 80px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #e5e5e5;
}

.btn-submit {
  padding: 10px 20px;
  background: #DC143C;
  color: white;
  border: 1px solid #DC143C;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #B22222;
  border-color: #B22222;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 货架不匹配提醒弹窗样式 */
.shelf-warning-modal {
  border: 3px solid #ff9800;
}

.shelf-warning-modal .modal-header {
  background: #fff3e0;
  border-bottom: 2px solid #ff9800;
}

.shelf-warning-modal .modal-header h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #e65100;
}

.warning-icon {
  font-size: 24px;
}

.warning-message {
  font-size: 16px;
  color: #333;
  line-height: 1.6;
  padding: 12px 16px;
  background: #fff8e1;
  border-left: 4px solid #ff9800;
  border-radius: 4px;
  margin-bottom: 16px;
}

.package-info {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #666;
  min-width: 80px;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.info-value.tracking-number {
  color: #DC143C;
  font-family: monospace;
  font-weight: 600;
}

.confirm-text {
  color: #666;
  font-size: 14px;
  text-align: center;
  margin: 0;
}

.shelf-warning-modal .modal-footer {
  justify-content: center;
}

.shelf-warning-modal .btn-submit {
  background: #ff9800;
  border-color: #ff9800;
}

.shelf-warning-modal .btn-submit:hover:not(:disabled) {
  background: #f57c00;
  border-color: #f57c00;
}
</style>

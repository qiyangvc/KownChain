// API 配置文件
import axios from 'axios'

// 基础URL配置
const BASE_URL = '/api'

// 创建axios实例
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000
})

// 请求拦截器 - 添加token等
apiClient.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应
apiClient.interceptors.response.use(
  response => {
    // 后端返回的数据结构是 {code, msg, data}
    const { data } = response
    if (data.code === 1) {
      // 成功
      return Promise.resolve(data.data)
    } else {
      // 业务错误
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
  },
  error => {
    // HTTP错误
    if (error.response?.status === 401) {
      // token过期，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
export { BASE_URL }

// 测试API连接的工具函数
import apiClient from './config'

export const apiTestUtils = {
  // 测试健康检查（无需认证）
  async testHealth() {
    try {
      const response = await fetch('/api/test/health')
      const data = await response.json()
      return {
        success: response.ok,
        data: data,
        message: response.ok ? '健康检查成功' : `健康检查失败: ${response.status}`
      }
    } catch (error) {
      return {
        success: false,
        message: `健康检查错误: ${error.message}`
      }
    }
  },

  // 测试JWT认证
  async testAuth() {
    try {
      const response = await apiClient.get('/test/auth')
      return {
        success: true,
        data: response,
        message: 'JWT认证测试成功'
      }
    } catch (error) {
      return {
        success: false,
        message: `JWT认证测试失败: ${error.message}`,
        error: error
      }
    }
  },

  // 检查本地存储的token
  checkToken() {
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')
    
    return {
      hasToken: !!token,
      hasUser: !!user,
      token: token ? `${token.substring(0, 20)}...` : null,
      user: user ? JSON.parse(user) : null
    }
  },

  // 运行所有测试
  async runAllTests() {
    console.log('🚀 开始API连接测试...')
    
    // 检查token状态
    const tokenStatus = this.checkToken()
    console.log('📋 Token状态:', tokenStatus)
    
    // 测试健康检查
    console.log('🔍 测试健康检查...')
    const healthResult = await this.testHealth()
    console.log('💡 健康检查结果:', healthResult)
    
    // 测试JWT认证（如果有token）
    if (tokenStatus.hasToken) {
      console.log('🔐 测试JWT认证...')
      const authResult = await this.testAuth()
      console.log('🔑 JWT认证结果:', authResult)
    } else {
      console.log('⚠️  跳过JWT认证测试 - 没有找到token')
    }
    
    console.log('✅ API连接测试完成')
  }
}

// 在浏览器控制台中暴露测试工具
if (typeof window !== 'undefined') {
  window.apiTestUtils = apiTestUtils
  window.runApiTests = () => apiTestUtils.runAllTests()
}

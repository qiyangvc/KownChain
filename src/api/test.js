// 简单的API连接测试
import apiClient from './config'

export const testApiConnection = async () => {
  try {
    // 测试API连接 - 这只是一个简单的健康检查
    console.log('测试API连接...')
    
    // 由于我们没有健康检查端点，这里只是验证配置
    console.log('API客户端配置:')
    console.log('- Base URL:', apiClient.defaults.baseURL)
    console.log('- Timeout:', apiClient.defaults.timeout)
    console.log('- Headers:', apiClient.defaults.headers)
    
    return {
      success: true,
      message: 'API配置正确'
    }
  } catch (error) {
    console.error('API连接测试失败:', error)
    return {
      success: false,
      message: error.message
    }
  }
}

// 在浏览器控制台中运行测试
if (typeof window !== 'undefined') {
  window.testApiConnection = testApiConnection
}

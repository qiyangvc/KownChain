// src/api/auth.js
import apiClient from './config'
import { mockApi } from '@/mock/mockApi'

// 使用模拟数据的标志 - 设置为false使用真实API
const USE_MOCK_DATA = false;

export default {
  // 登录
  async login(credentials) {
    if (USE_MOCK_DATA) {
      return mockApi.login(credentials);
    }
    try {
      const response = await apiClient.post('/user/login', {
        username: credentials.username,
        password: credentials.password
      })
      // 保存token到localStorage
      if (response.token) {
        localStorage.setItem('token', response.token)
        localStorage.setItem('user', JSON.stringify(response))
      }
      return { data: response }
    } catch (error) {
      throw error
    }
  },
  // 注册
  async register(userData) {
    if (USE_MOCK_DATA) {
      return mockApi.register(userData);
    }
    try {
      const requestData = {
        username: userData.userName,  // 修复字段名映射
        password: userData.password,
        mailbox: userData.email,
        confirmPassword: userData.password  // 添加确认密码字段
      };
      
      console.log('发送注册请求数据:', requestData);
      
      const response = await apiClient.post('/user/register', requestData)
      return { data: response }
    } catch (error) {
      console.error('注册API错误:', error);
      throw error
    }
  },

  // 重置密码请求
  async requestReset(email) {
    if (USE_MOCK_DATA) {
      return mockApi.requestReset(email);
    }
    try {
      const response = await apiClient.post('/user/request-reset', { 
        mailbox: email 
      })
      return { data: response }
    } catch (error) {
      throw error
    }
  },
  // 获取资源文件树
  async getResourceTree(userId) {
    if (USE_MOCK_DATA) {
      return mockApi.getResourceTree();
    }
    try {
      // 从localStorage获取用户信息
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const userIdToUse = userId || user.userid
      
      // 检查用户ID是否存在
      if (!userIdToUse) {
        throw new Error('用户ID不存在，请重新登录')
      }
      
      console.log('获取文件树，用户ID:', userIdToUse)
      const response = await apiClient.get(`/file/getAll/${userIdToUse}`)
      console.log('文件树API响应:', response)
      return { data: response }
    } catch (error) {
      console.error('获取文件树失败:', error)
      // 如果是认证错误，清除本地存储
      if (error.response?.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
      throw error
    }
  },  // 获取文件内容
  async getFileContent(fileId) {
    if (USE_MOCK_DATA) {
      return mockApi.getFileContent(fileId);
    }
    try {
      const response = await apiClient.get(`/file/content/${fileId}`)
      return { data: response }  // response就是文件内容字符串
    } catch (error) {
      throw error
    }
  },
  
  // 保存文件内容
  async saveFileContent(fileId, content) {
    if (USE_MOCK_DATA) {
      return mockApi.saveFileContent(fileId, content);
    }
    try {
      const response = await apiClient.post('/file/save', { 
        fileId: fileId,
        content: content
      })
      return { data: response }
    } catch (error) {      throw error
    }
  },

  // 创建文件或文件夹
  async createFile(fileName, parentId = null, isDirectory = false) {
    if (USE_MOCK_DATA) {
      return Promise.resolve({ data: '创建成功' });
    }
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const response = await apiClient.post('/file/create', null, {
        params: {
          userId: user.userid,
          fileName: fileName,
          parentId: parentId,
          isDirectory: isDirectory
        }
      })
      return { data: response }
    } catch (error) {
      throw error
    }
  },

  // 删除文件或文件夹
  async deleteFile(fileId, isDirectory = false) {
    if (USE_MOCK_DATA) {
      return Promise.resolve({ data: '删除成功' });
    }
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const response = await apiClient.delete('/file/delete', {
        params: {
          userId: user.userid,
          fileId: fileId,
          isDirectory: isDirectory
        }
      })
      return { data: response }
    } catch (error) {
      throw error
    }
  },

  // 登出
  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}
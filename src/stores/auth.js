import { defineStore } from 'pinia'
import authApi from '@/api/auth' 

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
    resourceTree: [],
    currentFile: null,
    currentFileContent: '',
    isLoadingTree: false,
    isLoadingContent: false,
    treeError: null,
    contentError: null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated && !!state.user
  },
  
  actions: {
    // 初始化认证状态
    initAuth() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      if (token && user) {
        this.isAuthenticated = true
        this.user = JSON.parse(user)
      }
    },

    async login(credentials) {
      try {
        const response = await authApi.login({
          username: credentials.userName, // 适配前端字段名
          password: credentials.password
        })
        
        this.isAuthenticated = true
        this.user = response.data
        localStorage.setItem('isAuthenticated', 'true')
        
        return response
      } catch (error) {
        console.error('Login failed:', error)
        throw error
      }
    },
    
    async register(userData) {
      try {
        const response = await authApi.register(userData)
        return response
      } catch (error) {
        console.error('Registration failed:', error)
        throw error
      }
    },
    
    async requestReset(email) {
      try {
        const response = await authApi.requestReset(email)
        return response
      } catch (error) {
        console.error('Password reset request failed:', error)
        throw error
      }
    },    async logout() {
      try {
        console.log('开始清理用户会话...')
        
        // 调用API清除存储(如果有服务端会话管理)
        await authApi.logout()
        
        // 清除store状态
        this.isAuthenticated = false
        this.user = null
        this.resourceTree = []
        this.currentFile = null
        this.currentFileContent = ''
        
        // 清除所有相关的localStorage项目
        this.clearAllAuthStorage()
        
        // 清除所有状态
        this.clearAllState()
        
        console.log('用户已登出，所有本地缓存已清除')
        
        return { success: true, message: '登出成功' }
      } catch (error) {
        console.error('登出过程中发生错误:', error)
        
        // 即使API调用失败，也要清理本地状态
        this.isAuthenticated = false
        this.user = null
        this.resourceTree = []
        this.currentFile = null
        this.currentFileContent = ''
        
        this.clearAllAuthStorage()
        
        this.clearAllState()
        
        console.log('强制清理本地缓存完成')
        
        return { success: true, message: '登出成功（本地清理）' }
      }
    },// 获取资源树
    async fetchResourceTree(userId = null) {
      // 如果已经在加载中，避免重复请求
      if (this.isLoadingTree) {
        console.log('资源树正在加载中，跳过重复请求')
        return this.resourceTree
      }
      
      this.isLoadingTree = true
      this.treeError = null
      
      try {
        console.log('开始获取资源树...')
        const response = await authApi.getResourceTree(userId)
        this.resourceTree = response.data || []
        console.log('资源树获取成功:', this.resourceTree)
        console.log('资源树详细数据:', JSON.stringify(this.resourceTree, null, 2))
        return this.resourceTree
      } catch (error) {
        console.error('Failed to fetch resource tree:', error)
        this.treeError = error.message || '获取资源树失败'
        
        // 清空资源树数据，避免显示旧数据
        this.resourceTree = []
        
        // 不重新抛出错误，避免影响页面渲染
        return []
      } finally {
        this.isLoadingTree = false
      }
    },
    
    // 获取文件内容
    async fetchFileContent(fileId) {
      this.isLoadingContent = true
      this.contentError = null
      
      try {
        const response = await authApi.getFileContent(fileId)
        this.currentFileContent = response.data
        return this.currentFileContent
      } catch (error) {
        console.error('Failed to fetch file content:', error)
        this.contentError = error.message || '获取文件内容失败'
        this.currentFileContent = ''
        throw error
      } finally {
        this.isLoadingContent = false
      }
    },

    // 保存文件内容
    async saveFileContent(fileId, content) {
      try {
        const response = await authApi.saveFileContent(fileId, content)
        this.currentFileContent = content
        return response
      } catch (error) {
        console.error('Failed to save file content:', error)
        throw error
      }
    },

    // 创建文件或文件夹
    async createFile(fileName, parentId = null, isDirectory = false) {
      try {
        const response = await authApi.createFile(fileName, parentId, isDirectory)
        // 创建成功后刷新资源树
        await this.fetchResourceTree()
        return response
      } catch (error) {
        console.error('Failed to create file:', error)
        throw error
      }
    },
    
    // 设置当前选中文件
    setCurrentFile(file) {
      this.currentFile = file
    },
    
    // 更新当前文件内容
    updateCurrentFileContent(content) {
      this.currentFileContent = content
    },
    
    // 关闭当前文件
    closeCurrentFile() {
      this.currentFile = null;
      this.currentFileContent = '';
      this.contentError = '';
      this.isLoadingContent = false;
    },

    // 清除错误状态
    clearErrors() {
      this.treeError = null
      this.contentError = null
    },

    // 清除所有状态（用于登出等场景）
    clearAllState() {
      this.resourceTree = []
      this.currentFile = null
      this.currentFileContent = ''
      this.isLoadingTree = false
      this.isLoadingContent = false
      this.treeError = null
      this.contentError = null
    },

    // 彻底清除所有认证相关的localStorage项目（安全清理）
    clearAllAuthStorage() {
      const authKeys = ['token', 'user', 'isAuthenticated']
      let clearedKeys = []
      
      authKeys.forEach(key => {
        if (localStorage.getItem(key)) {
          localStorage.removeItem(key)
          clearedKeys.push(key)
        }
      })
      
      console.log('已清除localStorage项目:', clearedKeys)
      return clearedKeys
    },
  }
})
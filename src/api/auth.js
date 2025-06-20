// src/api/auth.js
import apiClient from './config'
import { mockApi } from '@/mock/mockApi'
import axios from 'axios'

// 使用模拟数据的标志 - 设置为false使用真实API
const USE_MOCK_DATA = false;

export default {
  // 登录
  async login(credentials) {
    console.log('登录请求数据:', credentials);
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
        username: userData.username,  // 修复字段名映射
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
  },  // 删除文件或文件夹
  async deleteFile(fileId, isDirectory = false) {
    if (USE_MOCK_DATA) {
      return Promise.resolve({ data: '删除成功' });
    }
    try {
      const response = await apiClient.delete(`/file/deleteFileOrDir/${fileId}`)
      return { data: response }
    } catch (error) {
      throw error
    }
  },

  // 重命名文件或文件夹
  async renameFile(fileId, newName) {
    if (USE_MOCK_DATA) {
      return Promise.resolve({ data: '重命名成功' });
    }
    try {
      const response = await apiClient.put('/file/renameFileOrDir', null, {
        params: {
          fid: fileId,
          fName: newName
        }
      })
      return { data: response }
    } catch (error) {
      throw error
    }
  },

  // 移动文件或文件夹
  async moveFile(fileId, newParentId, userId) {
    if (USE_MOCK_DATA) {
      return Promise.resolve({ data: '移动成功' });
    }
    try {
      const response = await apiClient.put('/file/changeFileOrDirPosition', null, {
        params: {
          fid: fileId,
          parentFID: newParentId,
          userID: userId
        }
      })
      return { data: response }
    } catch (error) {
      throw error
    }
  },

  // 登出
  async logout() {
    // 清除localStorage中的认证信息
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('isAuthenticated')
    
    // 如果需要通知服务器登出，可以在这里添加API调用
    // 目前只做本地清理
    return Promise.resolve({ message: '登出成功' })
  },
    // 创建DDL
  async addDDL(data) {
    console.log('添加DDL请求数据:', data);
    if (USE_MOCK_DATA) {
      return mockApi.addDDL(data);
    }
    try {
      const response = await apiClient.post('/ddl/addDDL', null, {
        params:{
          title: data.dTitle,
          notes: data.dNotes,
          userid: data.uid,
          endtime: data.dEndTime
        }
      });
      console.log('添加DDL响应:', response);
      return { data: response };
    } catch (error) {
      throw error;
    }
  },

  // 删除DDL
  async deleteDDL(data){
    if (USE_MOCK_DATA) {
      return mockApi.deleteDDL(data);
    }
    try {
      const response = await apiClient.delete(`/ddl/deleteDDL`,{
        params:{
          did: data
        }
      });
      return { data: response };
    } catch (error) {
      throw error;
    }
  },

  // 修改DDL
  async modifyDDL(data){
    if (USE_MOCK_DATA) {
      return mockApi.modifyDDL(data);
    }
    try {
      const response = await apiClient.put('/ddl/modifyDDL', null,{
        params:{
          did: data.did,
          title: data.dTitle,
          notes: data.dNotes,
          endtime: data.dEndTime
        }
      });
      return { data: response };
    } catch (error) {
      throw error;
    }
  },

  // 获取DDL队列
  async getDDLByUid(uid) {
    if (USE_MOCK_DATA) {
      return mockApi.getDDLByUid(uid);
    }
    try {
      console.log('获取DDL请求参数:', uid);
      const response = await apiClient.get(`/ddl/getDDLByUid`,{
        params:{
          userid: uid
        }
      });
      console.log('获取DDL响应:', response);
      if (response.data && response.data.code !== 200) {
        console.error('获取DDL队列失败，错误码:', response.data.code, '错误信息:', response.data.message);
      }
      return { data: response };
    } catch (error) {
      console.error('获取DDL队列时发生异常:', error);
      throw error;
    }
  },
  // 新增待办
  async addTodo(data){
    if (USE_MOCK_DATA) {
      return mockApi.addTodo(data);
    }
    return axios.post('/addTodo', data);
  },

  // 删除待办
  deleteTodo(data) {
    if (USE_MOCK_DATA) {
      return mockApi.deleteTodo(data);
    }
    return axios.put('/deleteTodo', data);
  },

  // 修改待办
  modifyTodo(data) {
    if (USE_MOCK_DATA) {
      return mockApi.modifyTodo(data);
    }
    return axios.put('/modifyTodo', data);
  },

  // 按用户ID和日期获取待办列表
  getTodoByUidAndDate(params) {
    if (USE_MOCK_DATA) {
      return mockApi.getTodoByUidAndDate(params);
    }
    return axios.get('/getTodoByUidAndDate', { params });
  },
}
// src/api/auth.js
import axios from 'axios'
import { mockApi } from '@/mock/mockApi'
/*后期可能统一api*/
const API_URL = 'http://localhost:8080/api/auth' /*暂定*/
const RESOURCE_API_URL = 'http://localhost:8080/api/resource' /*资源API地址*/

// 使用模拟数据的标志
const USE_MOCK_DATA = true;

export default {
  // 登录
  login(credentials) {
    if (USE_MOCK_DATA) {
      return mockApi.login(credentials);
    }
    return axios.post(`${API_URL}/login`, credentials)
  },
  
  // 注册
  register(userData) {
    if (USE_MOCK_DATA) {
      return mockApi.register(userData);
    }
    return axios.post(`${API_URL}/register`, userData)
  },

  // 重置密码请求
  requestReset(email) {
    if (USE_MOCK_DATA) {
      return mockApi.requestReset(email);
    }
    return axios.post(`${API_URL}/request-reset`, { email })
  },

  // 获取资源文件树
  getResourceTree() {
    if (USE_MOCK_DATA) {
      return mockApi.getResourceTree();
    }
    return axios.get(`${RESOURCE_API_URL}/tree`);
  },
  
  // 获取文件内容
  getFileContent(url) {
    if (USE_MOCK_DATA) {
      return mockApi.getFileContent(url);
    }
    return axios.get(url);
  },
  
  // 保存文件内容
  saveFileContent(url, content) {
    if (USE_MOCK_DATA) {
      return mockApi.saveFileContent(url, content);
    }
    return axios.put(url, { content });
  },

  // 创建DDL
  addDDL(data) {
    if (USE_MOCK_DATA) {
      return mockApi.addDDL(data);
    }
    return axios.post('/addDDL', data);
  },

  // 删除DDL
  deleteDDL(data) {
    if (USE_MOCK_DATA) {
      return mockApi.deleteDDL(data);
    }
    return axios.put('/deleteDDL', data);
  },

  // 修改DDL
  modifyDDL(data) {
    if (USE_MOCK_DATA) {
      return mockApi.modifyDDL(data);
    }
    return axios.put('/modifyDDL', data);
  },

  // 获取DDL队列
  getDDLByUid(uid) {
    if (USE_MOCK_DATA) {
      return mockApi.getDDLByUid(uid);
    }
    return axios.get('/getDDLByUid', { params: { uid } });
  },

  // 新增待办
  addTodo(data) {
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
  }
}
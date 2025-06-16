import { use } from 'marked';
import { resourceTreeData } from './resourceTree';
import { userData } from './userdata';

// 模拟文件内容映射
const fileContents = {
  '/mock/files/vue-notes.md': () => import('./files/vue-notes.md?raw'),
  '/mock/files/readme.md': () => import('./files/readme.md?raw'),
  '/mock/files/react-basics.md': () => Promise.resolve('# React 基础\n\nReact 是一个用于构建用户界面的 JavaScript 库。'),
  '/mock/files/async-js.md': () => Promise.resolve('# JavaScript 异步编程\n\n## Promise\n\nPromise 是现代 JavaScript 中处理异步操作的标准方式。'),
  '/mock/files/functional-js.md': () => Promise.resolve('# JavaScript 函数式编程\n\n函数式编程是一种编程范式，它将计算视为数学函数的求值。'),
  '/mock/files/project-planning.md': () => Promise.resolve('# 项目规划\n\n## 开发时间线\n\n- 第一阶段：需求分析和设计\n- 第二阶段：核心功能开发\n- 第三阶段：测试和优化'),
  '/mock/files/api-docs.md': () => Promise.resolve('# API 文档\n\n## 用户认证\n\n- POST /api/auth/login\n- POST /api/auth/register\n\n## 资源管理\n\n- GET /api/resource/tree')
};

// 模拟延迟
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟API
let mockDDLList = [
  {
    dID: 1,
    uid: 1,
    dTitle: 'DDL 示例',
    dEndTime: '2025-06-20T18:00',
    dNotes: '示例DDL',
    dCreateTime: '2025-06-01T10:00'
  }
];

export const mockApi = {
  // 获取资源树
  async getResourceTree() {
    // 模拟网络延迟
    await delay(800);
    
    // 模拟成功响应
    return {
      data: resourceTreeData,
      status: 200
    };
  },
  
  // 获取文件内容
  async getFileContent(url) {
    // 模拟网络延迟
    await delay(500);
    
    // 检查URL是否存在于我们的映射中
    if (fileContents[url]) {
      try {
        // 获取文件内容
        const content = await fileContents[url]();
        
        // 如果是导入的模块，需要获取default
        const fileContent = content.default || content;
        
        return {
          data: fileContent,
          status: 200
        };
      } catch (error) {
        console.error('Error loading mock file:', error);
        throw { response: { status: 500, data: 'Error loading file' } };
      }
    } else {
      // 模拟404错误
      throw { 
        response: { 
          status: 404, 
          data: `File not found: ${url}` 
        } 
      };
    }
  },

  // 登录
  async login(credentials) {
    // 模拟网络延迟
    await delay(500);
    let key =0;
    // 检查用户名和密码
  while (key< userData.length) {
if (credentials.userName == userData[key].userName && credentials.password == userData[key].password) {
      return {
        userID: userData[key].userID,
        data: { 
          success: true,
          message: '登录成功'
        },
        status: 200
      };
    }
    key++;
    }

      throw { response: { status: 401, data: 'Invalid credentials' } };
    
  },
  // 注册
  async register(userD) {
    // 模拟网络延迟
    await delay(500);
    // 邮箱是否已注册
    const emailExists = userData.find(user => user.email === userD.email);
    if (emailExists) {
      throw { response: { status: 409, data: '邮箱已注册' } };
    }
    
    // 检查用户名是否已存在
    const existingUser = userData.find(user => user.userName === userD.userName);
    if (existingUser) {
      throw { response: { status: 409, data: '用户名已存在' } };
    }
    
    // 模拟成功注册
    const newUser = {
      userID: userData.length + 1,
      ...userD
    };
    userData.push(newUser);
    
    return {
      data: { 
        success: true, 
        message: '注册成功'
      },
      status: 201
    };
  },
  // 请求重置密码
  async requestReset(email) {
    // 模拟网络延迟
    await delay(500);
    // 检查邮箱是否存在
    const user = userData.find(user => user.email === email);
    if (!user) {
      throw { response: { status: 404, data: '邮箱未注册' } };
    }
    // 模拟成功响应
    return {
      data: { 
        success: true, 
        message: '重置密码请求已发送，请检查您的邮箱'
      },
      status: 200
    };
  },
  // 保存文件内容
  saveFileContent(url, content) {
    return new Promise((resolve) => {
      // 模拟网络延迟
      setTimeout(() => {
        // 在实际存储中更新文件内容
        // 这里仅模拟成功响应
        console.log(`模拟保存文件内容到 ${url}`, content);
        resolve({ 
          data: { 
            success: true, 
            message: '文件保存成功'
          }
        });
      }, 500);
    });
  },

  addDDL(data) {
    const newDDL = { ...data, dID: Date.now(), dCreateTime: new Date().toISOString() };
    mockDDLList.push(newDDL);
    return Promise.resolve({ data: { success: true, ddl: newDDL } });
  },

  deleteDDL(data) {
    mockDDLList = mockDDLList.filter(item => item.dID !== data.dID);
    return Promise.resolve({ data: { success: true } });
  },

  modifyDDL(data) {
    const idx = mockDDLList.findIndex(item => item.dID === data.dID);
    if (idx !== -1) {
      mockDDLList[idx] = { ...mockDDLList[idx], ...data };
    }
    return Promise.resolve({ data: { success: true, ddl: mockDDLList[idx] } });
  },

  getDDLByUid(uid) {
    const list = mockDDLList.filter(item => item.uid === uid);
    return Promise.resolve({ data: { success: true, list } });
  }
}

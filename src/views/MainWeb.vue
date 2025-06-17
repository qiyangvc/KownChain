<template>
  <div class="mainweb-container">
    <!-- 左侧功能选择区域 -->
    <div class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="toggle-btn" @click="toggleSidebar">
        <i class="toggle-icon" :class="isSidebarCollapsed ? 'icon-expand' : 'icon-collapse'"></i>
      </div>
      <nav>
        <router-link to="/mainweb/resource" class="nav-item sidebar-btn" :title="isSidebarCollapsed ? '资源管理' : ''">
          <span v-if="!isSidebarCollapsed" class="btn-text">资源管理</span>
          <span v-else class="icon">📚</span>
        </router-link>
        <router-link to="/mainweb/workbench" class="nav-item sidebar-btn" :title="isSidebarCollapsed ? '学习工作台' : ''">
          <span v-if="!isSidebarCollapsed" class="btn-text">学习工作台</span>
          <span v-else class="icon">🖥️</span>
        </router-link>
        <router-link to="/mainweb/schedule" class="nav-item sidebar-btn" :title="isSidebarCollapsed ? '日程管理' : ''">
          <span v-if="!isSidebarCollapsed" class="btn-text">日程管理</span>
          <span v-else class="icon">📅</span>
        </router-link>
        <router-link to="/mainweb/dashboard" class="nav-item sidebar-btn" :title="isSidebarCollapsed ? '学习看板' : ''">
          <span v-if="!isSidebarCollapsed" class="btn-text">学习看板</span>
          <span v-else class="icon">📊</span>
        </router-link>
      </nav>
      
      <!-- 用户信息和登出 -->
      <div class="user-section">
        <div v-if="!isSidebarCollapsed && authStore.user" class="user-info">
          <span class="username">{{ authStore.user.username }}</span>
        </div>
        <button @click="handleLogout" class="logout-btn sidebar-btn" :title="isSidebarCollapsed ? '登出' : ''">
          <span v-if="!isSidebarCollapsed" class="btn-text">登出</span>
          <span v-else class="icon">🚪</span>
        </button>
      </div>
    </div>
    
    <!-- 右侧工作区 -->
    <div class="content-area">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// 控制侧边栏展开/收起的状态
const isSidebarCollapsed = ref(false);

// 切换侧边栏状态的方法
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

// 处理登出
const handleLogout = async () => {
  try {
    console.log('开始执行登出操作...')
    
    // 执行登出操作
    await authStore.logout()
    
    console.log('登出成功，准备跳转到登录页面')
    
    // 跳转到登录页面
    await router.push('/login')
    
    console.log('已跳转到登录页面')
  } catch (error) {
    console.error('登出过程中发生错误:', error)
    // 即使发生错误，也要尝试跳转到登录页面
    router.push('/login')
  }
};
</script>

<style scoped>
/* 重置页面边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.mainweb-container {
  display: flex;
  height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;
  background-color: #ffffff; /* 添加白色背景 */
}

.sidebar {
  width: 200px;
  background-color: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  padding: 20px 0;
  margin: 0;
  padding-left: 0;
  position: relative;
  left: 0;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 50px;
}

.toggle-btn {
  position: absolute;
  right: -16px;
  top: 20px;
  width: 32px;
  height: 32px;
  background-color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  background-color: #f8f8f8;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
}

.toggle-icon {
  width: 14px;
  height: 14px;
  position: relative;
  transition: transform 0.3s ease;
  display: inline-block;
}

/* 重新定义图标样式 */
.icon-collapse::before,
.icon-collapse::after,
.icon-expand::before,
.icon-expand::after {
  content: '';
  position: absolute;
  background-color: #555;
  transition: all 0.3s ease;
}

/* 横线：两种状态都有 */
.icon-collapse::after,
.icon-expand::after {
  width: 14px;
  height: 2px;
  top: 6px;
  left: 0;
}

/* 竖线：只在展开状态(+)下显示 */
.icon-expand::before {
  width: 2px;
  height: 14px;
  top: 0;
  left: 6px;
}

/* 收起状态(-)不需要竖线 */
.icon-collapse::before {
  display: none;
}

.nav-item {
  display: block;
  width: 100%;
  padding: 12px 10px;
  text-decoration: none;
  color: #333;
  margin-bottom: 5px;
  text-align: left;
  overflow: hidden;
  white-space: nowrap;
}

.sidebar-btn {
  width: 100%;
  padding: 10px 16px;
  background: transparent;
  color: #22223b;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: background 0.2s, color 0.2s, border-radius 0.2s;
  margin-bottom: 8px;
  position: relative;
  overflow: hidden;
}

.sidebar-btn:hover {
  background: #f1f5fa;
  color: #1976d2;
}

.sidebar.collapsed .nav-item {
  text-align: center;
  padding: 12px 0;
}

.icon {
  font-size: 18px;
}

.nav-item:hover, .router-link-active {
  background-color: #e0e0e0;
  color: #000;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #ffffff; /* 添加白色背景 */
  color: #000000; /* 添加黑色文本颜色 */
}

/* 用户区域样式 */
.user-section {
  margin-top: auto;
  padding: 0 0 15px 0;
  border-top: 1px solid #e0e0e0;
}

.user-info {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 登出按钮专属红色 */
.logout-btn {
  background: #dc3545;
  color: #fff;
  margin-top: 18px; /* 新增：与分界线保持距离 */
}

.logout-btn:hover {
  background: #c82333;
  color: #fff;
  margin-top: 18px; /* 新增：与分界线保持距离 */
}

/* 收起时统一动画和布局 */
.sidebar.collapsed .sidebar-btn {
  justify-content: center;
  padding: 10px 0;
  border-radius: 50%;
  width: 38px;
  min-width: 38px;
  height: 38px;
  margin: 0 auto 12px auto;
  font-size: 20px;
  gap: 0;
  transition: all 0.2s;
}

/* 收起时只显示icon，隐藏文字 */
.sidebar.collapsed .sidebar-btn .btn-text {
  display: none;
}

/* icon样式 */
.sidebar-btn .icon {
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
}

/* 保证收起时登出按钮与分界线有距离 */
.sidebar.collapsed .logout-btn {
  margin-top: 18px !important;
}
</style>

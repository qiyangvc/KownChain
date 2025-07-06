<template>
  <!-- 仅在根路径显示欢迎界面 -->
  <div v-if="$route.path === '/'">
    <div class="welcome-container">
      <h1>欢迎使用知链 KnowChain</h1>
      <p class="subtitle">知识库笔记平台</p>
      <router-link to="/login">
        <button class="login-button">立即登录</button>
      </router-link>
    </div>
    
  </div>
  
  
  <!-- 路由视图容器 -->
  <router-view></router-view>
</template>

<script setup>
import { useRoute } from 'vue-router'

const $route = useRoute()
</script>

<style scoped>
.welcome-container {
  text-align: center;
  padding: 2rem;
  /* max-width: ; */
  margin: 5rem auto;
}

h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin-bottom: 3rem;
}

.login-button {
  background: #3498db;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 4px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.3s;
}

.login-button:hover {
  background: #2980b9;
}

</style>
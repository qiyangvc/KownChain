# Vue 学习笔记

## Vue 3 基础

[README.md](/mock/files/readme.md)

Vue 3 是一个用于构建用户界面的渐进式框架。与 Vue 2 相比，它带来了许多重要的改进：

- **Composition API**：提供了更灵活的代码组织方式
- **更好的 TypeScript 支持**
- **更小的包体积**
- **改进的性能**

### 创建 Vue 应用

```js
import { createApp } from 'vue'
import App from './App.vue'

const app = createApp(App)
app.mount('#app')
```

### 组合式 API 示例

```js
import { ref, computed, onMounted } from 'vue'

export default {
  setup() {
    // 响应式状态
    const count = ref(0)
    
    // 计算属性
    const doubleCount = computed(() => count.value * 2)
    
    // 方法
    function increment() {
      count.value++
    }
    
    // 生命周期钩子
    onMounted(() => {
      console.log('组件已挂载')
    })
    
    // 暴露给模板
    return {
      count,
      doubleCount,
      increment
    }
  }
}
```

## Vue Router

Vue Router 是 Vue.js 的官方路由管理器，它与 Vue.js 核心深度集成，使构建单页面应用变得轻而易举。

### 基本使用

```js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: Home },
  { path: '/about', component: About }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
```

## Pinia 状态管理

Pinia 是 Vue 的状态管理库，提供了一个简单直观的 API。

### 创建 Store

```js
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', {
  state: () => ({ count: 0 }),
  getters: {
    doubleCount: (state) => state.count * 2
  },
  actions: {
    increment() {
      this.count++
    }
  }
})
```

import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 静态导入所有组件，避免动态导入可能的缓存问题
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import FindPassword from '@/views/FindPassword.vue'
import MainWeb from '@/views/MainWeb.vue'
import ResourceView from '@/views/ResourceView.vue'
import WorkbenchView from '@/views/WorkbenchView.vue'
import ScheduleView from '@/views/ScheduleView.vue'
import DashboardView from '@/views/DashboardView.vue'

const routes = [
  {
    path: '/',
    name: 'Welcome',
    redirect: '/login' // 重定向到登录页
  },  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresGuest: true } // 已登录用户不能访问
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { requiresGuest: true }
  },
  {
    path: '/reset',
    name: 'FindPassword',
    component: FindPassword,
    meta: { requiresGuest: true }
  },
  {
    path: '/mainweb',
    name: 'MainWeb',
    component: MainWeb,
    redirect: '/mainweb/resource',
    meta: { requiresAuth: true }, // 需要认证
    children: [
      {
        path: 'resource',
        name: 'Resource',
        component: ResourceView,
        meta: { requiresAuth: true }
      },
      {
        path: 'workbench',
        name: 'Workbench',
        component: WorkbenchView,
        meta: { requiresAuth: true }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: ScheduleView,
        meta: { requiresAuth: true }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: DashboardView,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 清除可能影响页面渲染的错误状态
  if (from.path !== to.path) {
    console.log(`路由切换: ${from.path} -> ${to.path}`)
    
    // 清除错误状态，确保路由切换正常
    authStore.clearErrors()
    
    // 如果从 resource 或 workbench 切换到其他页面，清除相关状态
    if ((from.name === 'Resource' || from.name === 'Workbench') && 
        (to.name === 'Schedule' || to.name === 'Dashboard')) {
      console.log('从文件相关页面切换到其他页面，清理状态')
      // 清除当前文件状态，但保留资源树
      authStore.closeCurrentFile()
    }
  }
  
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!authStore.isLoggedIn) {
      // 未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路径
      })
      return
    }
  }
  
  // 检查是否是访客页面（已登录用户不应访问）
  if (to.matched.some(record => record.meta.requiresGuest)) {
    if (authStore.isLoggedIn) {
      // 已登录，重定向到主页
      next('/mainweb')
      return
    }
  }
  
  next()
})

export default router
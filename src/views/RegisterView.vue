<!-- Register.vue -->
<template>
    <div class="login-container">
      <form @submit.prevent="handleSubmit" class="login-form">
        <h2>用户注册</h2>
        
        <div class="form-group">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="form.username" 
            required
            @input="validateField('username')"
          />
          <span class="error" v-if="errors.username">{{ errors.username }}</span>
        </div>
  
        <div class="form-group">
          <label>邮箱</label>
          <input 
            type="email" 
            v-model="form.email" 
            required
            @input="validateField('email')"
          />
          <span class="error" v-if="errors.email">{{ errors.email }}</span>
        </div>
  
        <div class="form-group">
          <label>密码</label>
          <input 
            type="password" 
            v-model="form.password" 
            required
            @input="validateField('password')"
          >
          <span class="error" v-if="errors.password">{{ errors.password }}</span>
        </div>
  
        <div class="form-group">
          <label>确认密码</label>
          <input 
            type="password" 
            v-model="form.confirmPassword" 
            required
            @input="validateField('confirmPassword')"
          >
          <span class="error" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
        </div>
  
        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '注册中...' : '注册' }}
        </button>
        <div class="form-group">
          <label>
            已有账号？<router-link to="/login">登录</router-link>
          </label>
        </div>
        <div class="error" v-if="registerError">{{ registerError }}</div>
        <div>{{ response }}</div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAuthStore } from '@/stores/auth'
  
  const router = useRouter()
  const authStore = useAuthStore()
  
  const form = reactive({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  })
  
  const errors = reactive({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  })
  
  const isSubmitting = ref(false)
  const registerError = ref('')
  const response = ref('')
  const validationRules = {
    username: (value) => {
      if (!value) return '用户名不能为空'
      if (value.length < 3 || value.length > 20) return '用户名长度应在3到20个字符之间'
      return ''
    },
    email: (value) => {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!value) return '邮箱不能为空'
      if (!emailPattern.test(value)) return '邮箱格式不正确'
      return ''
    },
    password: (value) => {
      if (!value) return '密码不能为空'
      if (value.length < 6 || value.length > 20) return '密码长度应在6到20个字符之间'
      return ''
    },
    confirmPassword: (value) => {
      if (!value) return '请确认密码'
      if (value !== form.password) return '两次输入的密码不一致'
      return ''
    }
  }
  
  const validateField = (field) => {
    if(!(field in validationRules)) {
      console.warn(`字段 ${field} 缺少验证规则`)
      return
    }
    const error = validationRules[field](form[field])
    errors[field] = error ||''
  }
    const handleSubmit = async () => {
    try {
      Object.keys(form).forEach(validateField)
      const hasErrors = Object.values(errors).some(Boolean)
      if (hasErrors) {
        registerError.value = '请正确填写所有必填字段'
        return
      }
      
      isSubmitting.value = true
      const response = await authStore.register({
        username: form.username, 
        password: form.password, 
        email: form.email
      })
      
      console.log('注册响应:', response)
      
      // 修复响应处理逻辑
      if (response && response.data) {
        // 注册成功，跳转到登录页
        router.push('/login')
      } else {
        registerError.value = '注册失败，请重试'
      }
    } catch (error) {
      console.error('注册错误:', error)
      // 修复错误处理逻辑
      if (error.response && error.response.data) {
        registerError.value = error.response.data.message || error.response.data || '注册失败'
      } else {
        registerError.value = error.message || '注册失败，请重试'
      }
    } finally {
      isSubmitting.value = false
    }
  }
  </script>
  <style scoped>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    width: min(90vw, 600px); /* 最大600px，最小90%视宽 */
    min-height: min(80vh, 560px); /* 高度自适应 */
    transition:
    color 0.5s,
    background-color 0.5s;
  }
  @media (min-width: 768px) {
  .login-form {
    padding: 4rem 3rem; /* 大屏增加内边距 */
    transition:
    color 0.5s,
    background-color 0.5s;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    width: 100%;
    max-width: 400px;
  }
}
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
  }
  
  input {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  button {
    width: 100%;
    padding: 0.75rem;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
  
  .error {
    color: #dc3545;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }
  </style>
<!-- ResetPassword.vue -->
<template>
    <div class="login-container">
      <form @submit.prevent="handleSubmit" class="login-form">
        <h2>重置密码</h2>
        
        <div class="form-group">
          <label>注册邮箱</label>
          <input 
            type="email" 
            v-model="form.email" 
            required
            @input="validateField('email')"
          />
          <span class="error" v-if="errors.email">{{ errors.email }}</span>
        </div>
  
        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '发送中...' : '发送重置邮件' }}
        </button>
        <div class="form-group">
          <label>
            想起密码了？<router-link to="/login">返回登录</router-link>
          </label>
        </div>
        <div class="success" v-if="successMessage">{{ successMessage }}</div>
        <div class="error" v-if="resetError">{{ resetError }}</div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useAuthStore } from '@/stores/auth'
  
  const authStore = useAuthStore()
  
  const form = reactive({
    email: ''
  })
  
  const errors = reactive({
    email: ''
  })
  
  const isSubmitting = ref(false)
  const resetError = ref('')
  const successMessage = ref('')
  
  const validationRules = {
    email: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || '邮箱格式不正确'
  }
  
  const validateField = (field) => {
    const rule = validationRules[field]
    if (rule) {
      const error = rule(form[field])
      errors[field] = typeof error === 'string' ? error : ''
    }
  }
  /* 这里重置密码的方法存疑*/
  const handleSubmit = async () => {
    isSubmitting.value = true
    resetError.value = ''
    successMessage.value = ''
    
    // 验证所有字段
    for (const field in form) {
      validateField(field)
      if (errors[field]) {
        isSubmitting.value = false
        return
      }
    }
      try {
      // 调用重置密码 API
      const response = await authStore.requestReset(form.email)
      successMessage.value = '重置邮件已发送，请检查您的邮箱。'
      resetError.value = '' // 清空错误信息
      form.email = '' // 清空输入框
    } catch (error) {
      console.error('重置密码请求失败:', error)
      
      // 优化错误处理，提取具体的错误信息
      let errorMessage = '发送重置邮件失败，请稍后再试。'
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message
      } else if (error.response?.data) {
        errorMessage = error.response.data
      } else if (error.message) {
        errorMessage = error.message
      }
      
      resetError.value = errorMessage
      successMessage.value = '' // 清空成功信息
    } finally {
      isSubmitting.value = false
    }
  }
  </script>
  
  <style scoped>
  .success {
    color: #28a745;
    font-size: 0.875rem;
    margin-top: 1rem;
  }
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
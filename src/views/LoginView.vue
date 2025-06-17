<template>
    <div class="login-container">
      <form @submit.prevent="handleSubmit" class="login-form">
        <h2>用户登录</h2>
        
        <div class="form-group">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="form.userName" 
            required
            @input="validateField('userName')"
          />
          <span class="error" v-if="errors.userName">{{ errors.userName }}</span>
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
          <label>
            忘记密码？<router-link to="/reset">重置密码</router-link>
          </label>
        </div>
        <button type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? '登录中...' : '登录' }}
        </button>
        <div class="form-group">
          <label>
            没有账号？<router-link to="/register">注册</router-link>
          </label>
        </div>
        <div class="error" v-if="loginError">{{ loginError }}</div>
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
    userName: '',
    password: ''
  })
  
  const errors = reactive({
    userName: '',
    password: ''
  })
  
  const isSubmitting = ref(false)
  const loginError = ref('')
  const response = ref('')

  const validationRules = {
  userName: value => 
    value.length >= 3 ? '' : '用户名至少3个字符',
  
  password: value => {
    if (value.length < 6) return '密码至少6个字符'
    //if (!/[A-Z]/.test(value)) return '需要包含大写字母'
    return ''
  }
}
  
  const validateField = (field) => {
  // 防御性编程
  if (!(field in validationRules)) {
    console.warn(`字段 ${field} 缺少验证规则`)
    return
  }
  
  const result = validationRules[field](form[field])
  errors[field] = result || '' // 明确处理空值
}
  
  const handleSubmit = async () => {
  try {
    Object.keys(form).forEach(validateField)
    // await nextTick()
    const hasErrors = Object.values(errors).some(Boolean)
    if (hasErrors) {
      loginError.value = errors||'请正确填写所有必填字段'
      return
    }    isSubmitting.value = true
    loginError.value = null
    const response = await authStore.login(form)
    form.password = ''
    
    // 处理登录后的重定向
    const redirectPath = router.currentRoute.value.query.redirect || '/mainweb/resource'
    router.push(redirectPath)
    
  } catch (error) {
    handleLoginError(error)
  } finally {
    isSubmitting.value = false
  }
}

// 集中式错误处理器
const handleLoginError = (error) => {
  // 网络错误处理
  if (error.isAxiosError && !error.response) {
    loginError.value = '网络连接异常，请检查网络设置'
    return
  }

  // 后端返回的标准错误
  const statusCode = error.response?.status
  switch (statusCode) {
    case 401:
      loginError.value = '用户名或密码不正确'
      break
    case 429:
      loginError.value = '尝试次数过多，请稍后重试'
      break
    default:
      // printf('未知错误：', error)
      loginError.value = error.response?.data?.message || error.message || '登录失败'
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
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
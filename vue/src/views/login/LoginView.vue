<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-banner">
        <div class="banner-content">
          <h1>教辅资料订购系统</h1>
          <p>便捷、高效的教辅资料管理平台</p>
        </div>
      </div>
      <div class="login-form">
        <el-card class="login-card">
          <template #header>
            <h2>用户登录</h2>
          </template>
          
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="rules"
            label-position="top"
            size="large"
          >
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="loginForm.username"
                placeholder="请输入用户名"
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>

            <div class="register-link">
              还没有账号？
              <router-link to="/register">去注册</router-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { login, getUserInfo } from '@/api'
import { useUserStore } from '@/store/user'
import { UserRole } from '@/types'
import type { ApiResponse } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const loginData = {
      username: loginForm.value.username,
      password: loginForm.value.password
    }
    
    console.log('Attempting login with:', { username: loginData.username })
    
    const response = await login(loginData)
    console.log('Login response:', response)
    
    if (response.data.success) {
      const token = response.data.data
      console.log('Received token:', token)
      
      if (!token) {
        throw new Error('登录成功但未收到有效token')
      }
      
      // 先清除旧的token
      localStorage.removeItem('token')
      userStore.logout()
      
      // 设置新token
      localStorage.setItem('token', token)
      userStore.setToken(token)
      
      try {
        // 解析token中的角色信息
        const tokenPayload = JSON.parse(atob(token.split('.')[1]))
        const userRole = tokenPayload.role
        
        // 存储基本用户信息
        userStore.user = {
          username: loginData.username,
          role: userRole
        }
        
        // 验证token是否正确设置
        const storedToken = localStorage.getItem('token')
        if (!storedToken) {
          throw new Error('Token未能正确存储')
        }
        
        ElMessage.success('登录成功')
        
        // 根据角色跳转
        const targetPath = userRole === 'ADMIN' ? '/app/manage' : '/app/textbooks'
        console.log('Redirecting to:', targetPath)
        
        // 使用 replace 而不是 push，避免浏览器返回到登录页
        await router.replace(targetPath)
        
      } catch (parseError) {
        console.error('Token parsing error:', parseError)
        ElMessage.error('登录信息处理失败，请重试')
        userStore.logout()
        localStorage.removeItem('token')
      }
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error: any) {
    console.error('Login error:', error)
    if (error.response) {
      console.error('Error response:', error.response)
      ElMessage.error(error.response.data?.message || '登录失败，请重试')
    } else {
      ElMessage.error(error.message || '登录失败，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  min-width: 1200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dark-bg);
  padding: 40px;

  .login-content {
    width: 1200px;
    height: 600px;
    display: flex;
    background: var(--dark-secondary);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .login-banner {
      flex: 1;
      background: linear-gradient(135deg, var(--primary-color), #00896b);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;

      .banner-content {
        text-align: center;
        color: var(--text-light);

        h1 {
          font-size: 36px;
          font-weight: 500;
          margin: 0 0 20px;
        }

        p {
          font-size: 18px;
          opacity: 0.9;
          margin: 0;
        }
      }
    }

    .login-form {
      width: 480px;
      padding: 40px;
      display: flex;
      align-items: center;

      .login-card {
        width: 100%;
        background: transparent;
        box-shadow: none;

        :deep(.el-card__header) {
          padding: 0 0 24px;
          border-bottom: none;

          h2 {
            font-size: 24px;
            color: var(--text-light);
            text-align: center;
            margin: 0;
          }
        }

        :deep(.el-card__body) {
          padding: 0;
        }

        .el-form-item {
          margin-bottom: 24px;

          &:last-child {
            margin-bottom: 0;
          }

          :deep(.el-form-item__label) {
            color: var(--text-light);
            padding-bottom: 8px;
            font-size: 14px;
          }

          :deep(.el-input__wrapper) {
            background-color: var(--dark-bg);
            border: 1px solid var(--dark-bg);
            box-shadow: none;
            height: 44px;

            &.is-focus {
              border-color: var(--primary-color);
            }

            input {
              color: var(--text-light);
              height: 44px;
              font-size: 14px;

              &::placeholder {
                color: var(--text-gray);
              }
            }
          }
        }

        .login-button {
          width: 100%;
          height: 44px;
          font-size: 16px;
          border-radius: 4px;
          margin-top: 8px;
        }

        .register-link {
          text-align: center;
          margin-top: 24px;
          color: var(--text-gray);
          font-size: 14px;

          a {
            color: var(--primary-color);
            text-decoration: none;
            margin-left: 4px;

            &:hover {
              text-decoration: underline;
            }
          }
        }
      }
    }
  }
}
</style> 
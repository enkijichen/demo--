<template>
  <div class="register-container">
    <div class="register-content">
      <div class="register-banner">
        <div class="banner-content">
          <h1>教辅资料订购系统</h1>
          <p>便捷、高效的教辅资料管理平台</p>
        </div>
      </div>
      <div class="register-form">
        <el-card class="register-card">
          <template #header>
            <h2>用户注册</h2>
          </template>
          
          <el-form
            ref="formRef"
            :model="registerForm"
            :rules="rules"
            label-position="top"
            size="large"
          >
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="registerForm.username"
                placeholder="请输入用户名"
              />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="registerForm.email"
                placeholder="请输入邮箱"
              />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input 
                v-model="registerForm.phone"
                placeholder="请输入手机号"
              />
            </el-form-item>
            
            <el-form-item>
              <el-checkbox v-model="registerForm.isAdmin">注册为管理员</el-checkbox>
            </el-form-item>

            <el-form-item v-if="registerForm.isAdmin" prop="adminKey" label="管理员密钥">
              <el-input 
                v-model="registerForm.adminKey"
                type="password"
                placeholder="请输入管理员密钥"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                class="register-button"
                :loading="loading"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>

            <div class="login-link">
              已有账号？
              <router-link to="/login">去登录</router-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { RegisterForm, RegisterRequest } from '@/types'
import { register } from '@/api'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = ref<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  isAdmin: false,
  adminKey: ''
})

// 密码验证规则
const validatePass = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.value.confirmPassword !== '') {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  adminKey: [
    { 
      required: false, 
      message: '请输入管理员密钥', 
      trigger: 'blur',
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (registerForm.value.isAdmin && !value) {
          callback(new Error('请输入管理员密钥'))
        } else {
          callback()
        }
      }
    }
  ]
})

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        console.log('Form data:', registerForm.value) // 调试日志
        
        // 构建注册请求数据
        const registerData: RegisterRequest = {
          username: registerForm.value.username,
          password: registerForm.value.password,
          email: registerForm.value.email,
          phone: registerForm.value.phone,
          adminKey: registerForm.value.isAdmin ? registerForm.value.adminKey : ''
        }

        console.log('Request data:', registerData) // 调试日志
        
        const response = await register(registerData)
        
        if (response.data.success) {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error(response.data.message || '注册失败')
        }
      } catch (error: unknown) {
        const err = error as { response?: { data?: { message?: string } } }
        console.error('注册失败:', error)
        ElMessage.error(err.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dark-bg);
  padding: 40px;

  .register-content {
    width: 1200px;
    display: flex;
    background: var(--dark-secondary);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .register-banner {
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

    .register-form {
      width: 480px;
      padding: 40px;

      .register-card {
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

          :deep(.el-form-item__label) {
            color: var(--text-light);
            padding-bottom: 8px;
          }

          :deep(.el-input__wrapper) {
            background-color: var(--dark-bg);
            border: 1px solid var(--dark-bg);
            box-shadow: none;

            &.is-focus {
              border-color: var(--primary-color);
            }

            input {
              color: var(--text-light);
              height: 40px;

              &::placeholder {
                color: var(--text-gray);
              }
            }
          }
        }

        .register-button {
          width: 100%;
          height: 40px;
        }

        .login-link {
          text-align: center;
          margin-top: 16px;
          color: var(--text-gray);

          a {
            color: var(--primary-color);
            text-decoration: none;

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
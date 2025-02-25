<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
          <el-tag :type="userStore.isAdmin ? 'danger' : 'success'">
            {{ userStore.isAdmin ? '管理员' : '普通用户' }}
          </el-tag>
        </div>
      </template>

      <el-descriptions 
        :column="1" 
        border
        class="profile-descriptions"
      >
        <el-descriptions-item label="用户名">
          {{ userStore.user?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ userStore.user?.email }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ userStore.user?.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          {{ userStore.isAdmin ? '管理员' : '普通用户' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="password-section">
        <h3>修改密码</h3>
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input 
              v-model="passwordForm.oldPassword"
              type="password"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword"
              type="password"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword"
              type="password"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleUpdatePassword"
              :loading="loading"
            >
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updatePassword, getUserInfo } from '@/api'

const userStore = useUserStore()
const passwordFormRef = ref<FormInstance>()
const loading = ref(false)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchUserInfo = async () => {
  try {
    const response = await getUserInfo({ username: userStore.user?.username });
    if (response.data.success) {
      userStore.user = response.data.data;
    }
  } catch (error) {
    console.error('Error fetching user info:', error);
  }
}

onMounted(() => {
  if (userStore.user?.username) {
    fetchUserInfo();
  }
})

const validatePass = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (passwordForm.value.confirmPassword !== '') {
      if (passwordFormRef.value) {
        passwordFormRef.value.validateField('confirmPassword')
      }
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await updatePassword({
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        
        if (response.data.success) {
          ElMessage.success('密码修改成功')
          passwordForm.value = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
        } else {
          ElMessage.error(response.data.message || '密码修改失败')
        }
      } catch (error: any) {
        console.error('密码修改失败:', error)
        ElMessage.error(error.response?.data?.message || '密码修改失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.profile-container {
  padding: 30px;
  max-width: 800px;
  margin: 0 auto;

  .profile-card {
    background-color: var(--dark-secondary);
    border: none;

    :deep(.el-card__header) {
      border-bottom: 1px solid var(--border-color);
      padding: 20px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        h2 {
          margin: 0;
          color: var(--text-light);
          font-size: 20px;
        }
      }
    }

    :deep(.el-descriptions) {
      --el-descriptions-item-bordered-label-background: var(--dark-bg);
      --el-descriptions-item-bordered-content-background: var(--dark-secondary);
      --el-descriptions-border-color: var(--border-color);
      
      .el-descriptions__cell {
        padding: 16px 24px;
      }

      .el-descriptions__label {
        color: var(--text-gray);
        font-weight: normal;
      }

      .el-descriptions__content {
        color: var(--text-light);
      }

      .el-descriptions__body {
        background-color: transparent;
      }

      .el-descriptions__table {
        border-color: var(--border-color);
        
        td {
          border-color: var(--border-color);
        }
      }
    }

    .password-section {
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid var(--border-color);

      h3 {
        color: var(--text-light);
        margin: 0 0 20px;
      }

      :deep(.el-form) {
        max-width: 500px;

        .el-form-item__label {
          color: var(--text-light);
        }

        .el-input__wrapper {
          background-color: var(--dark-bg);
          border: 1px solid var(--border-color);
          box-shadow: none;

          &.is-focus {
            border-color: var(--primary-color);
          }

          input {
            color: var(--text-light);
            
            &::placeholder {
              color: var(--text-gray);
            }
          }
        }

        .el-button {
          width: 100%;
          margin-top: 12px;
        }
      }
    }
  }
}
</style> 
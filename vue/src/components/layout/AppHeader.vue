<template>
  <div class="app-header">
    <h1>教辅资料订购系统</h1>
    <div class="user-info">
      <span>{{ userStore.username }}</span>
      <el-button type="text" @click="handleLogout">退出</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.app-header {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h1 {
    margin: 0;
    font-size: 20px;
    color: var(--text-light);
    font-weight: 500;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    color: var(--text-light);

    .el-button {
      color: var(--text-light);
      
      &:hover {
        color: var(--primary-color);
      }
    }
  }
}
</style> 
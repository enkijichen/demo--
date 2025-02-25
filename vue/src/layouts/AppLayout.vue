<template>
  <el-container class="app-layout">
    <el-aside>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        :collapse="isCollapse"
      >
        <el-menu-item index="/app/textbooks">
          <el-icon><Document /></el-icon>
          <template #title>教材列表</template>
        </el-menu-item>
        <el-menu-item index="/app/orders">
          <el-icon><List /></el-icon>
          <template #title>订单列表</template>
        </el-menu-item>
        <el-menu-item 
          v-if="userStore.isAdmin" 
          index="/app/manage"
        >
          <el-icon><Setting /></el-icon>
          <template #title>教材管理</template>
        </el-menu-item>
        <el-menu-item 
          v-if="userStore.isAdmin" 
          index="/app/order-manage"
        >
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header height="60px">
        <div class="header-content">
          <el-button
            type="text"
            @click="toggleCollapse"
            class="toggle-button"
          >
            <el-icon>
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
          <div class="user-info">
            <el-dropdown>
              <span class="user-dropdown">
                {{ userStore.user?.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/app/profile')">
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-main class="content-main">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <keep-alive :include="['TextbookManage']">
              <component :is="Component" />
            </keep-alive>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Document, List, Setting, Expand, Fold } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { UserRole } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)
const isAdmin = computed(() => userStore.user?.role === UserRole.ADMIN)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
  } catch (error) {
    // 用户取消退出登录
  }
}
</script>

<style scoped lang="scss">
.app-layout {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
}

.el-aside {
  width: 220px !important;
  flex-shrink: 0;
  background-color: var(--dark-secondary);
  border-right: 1px solid var(--border-color);
  
  .el-menu {
    height: 100%;
    border-right: none;
    background-color: transparent;
  }
}

.main-container {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  
  .el-header {
    height: 60px;
    line-height: 60px;
    background-color: var(--dark-secondary);
    border-bottom: 1px solid var(--border-color);
    padding: 0 20px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100%;

      .toggle-button {
        color: var(--text-light);
      }
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 20px;
        color: var(--text-light);
      }
    }
  }
  
  .content-main {
    flex: 1;
    background-color: var(--dark-bg);
    padding: 0;
    overflow: auto;

    .content-wrapper {
      min-width: 1200px;
      height: 100%;
    }
  }
}

:deep(.el-menu-item) {
  color: var(--text-light) !important;
  
  &.is-active {
    background-color: var(--dark-hover) !important;
    color: var(--primary-color) !important;
    font-weight: 500;
    
    .el-icon {
      color: var(--primary-color) !important;
    }
  }
  
  &:hover {
    background-color: var(--dark-hover);
    color: var(--primary-color) !important;
    
    .el-icon {
      color: var(--primary-color) !important;
    }
  }
  
  .el-icon {
    color: var(--text-light);
    font-size: 18px;
  }
}

.el-menu {
  --el-menu-text-color: var(--text-light) !important;
  --el-menu-hover-text-color: var(--primary-color) !important;
  --el-menu-bg-color: var(--dark-secondary) !important;
  --el-menu-hover-bg-color: var(--dark-hover) !important;
  --el-menu-active-color: var(--primary-color) !important;
}

.user-dropdown {
  color: var(--text-light);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

:root {
  // 定义暗色主题的颜色变量
  --dark-bg: #1a1a1a;
  --dark-secondary: #242424;
  --dark-hover: #2c2c2c;
  --border-color: #333;
  --text-light: #e5e5e5;
  --text-gray: #a0a0a0;
  --primary-color: #409eff;
}
</style> 
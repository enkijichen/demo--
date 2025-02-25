<template>
  <el-aside width="var(--sidebar-width)" class="sidebar-container">
    <el-menu
      class="sidebar-menu"
      :default-active="route.path"
      router
      background-color="var(--dark-secondary)"
      text-color="var(--text-light)"
      active-text-color="var(--primary-color)"
    >
      <el-menu-item index="/app/textbooks">
        <el-icon><Document /></el-icon>
        <span>教材列表</span>
      </el-menu-item>
      <el-menu-item index="/app/orders">
        <el-icon><List /></el-icon>
        <span>我的订单</span>
      </el-menu-item>
      <el-menu-item v-if="userStore.isAdmin" index="/app/manage">
        <el-icon><Setting /></el-icon>
        <span>教材管理</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Document, List, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
</script>

<style scoped lang="scss">
.sidebar-container {
  background: var(--dark-secondary);
  border-right: 1px solid var(--dark-bg);
  position: fixed;
  top: var(--header-height);
  bottom: 0;
  left: 0;
  z-index: 99;

  .sidebar-menu {
    border-right: none;
    padding: 16px 0;

    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
      margin: 4px 16px;
      border-radius: 4px;
      font-size: 16px;
      
      &.is-active {
        background: var(--dark-bg);
      }

      &:hover {
        background: var(--dark-bg);
      }

      .el-icon {
        font-size: 18px;
        margin-right: 12px;
      }
    }
  }
}
</style> 
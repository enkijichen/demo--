<template>
  <el-container class="app-layout">
    <el-header class="header" height="var(--header-height)">
      <div class="header-content">
        <AppHeader />
      </div>
    </el-header>
    <el-container class="main-container">
      <AppSidebar />
      <el-main class="content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
</script>

<style scoped lang="scss">
.app-layout {
  min-height: 100vh;
  background: var(--dark-bg);

  .header {
    padding: 0;
    background: var(--dark-secondary);
    border-bottom: 1px solid var(--dark-bg);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;

    .header-content {
      width: 100%;
      padding: 0 40px;
      height: 100%;
    }
  }

  .main-container {
    padding-top: var(--header-height);
    min-height: 100vh;
  }

  .content {
    margin-left: var(--sidebar-width);
    background: var(--dark-bg);
    padding: 24px 40px;
    min-width: 1200px;
  }
}
</style> 
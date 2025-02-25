<template>
  <el-button
    :icon="themeStore.isDark ? Sunny : Moon"
    circle
    @click="themeStore.toggleTheme"
  />
</template>

<script setup lang="ts">
import { Moon, Sunny } from '@element-plus/icons-vue'
import { useThemeStore } from '@/store/theme'

const themeStore = useThemeStore()
</script> 
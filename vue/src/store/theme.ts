import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem('theme') === 'dark')
  
  const themes = {
    light: {
      primary: '#00b96b',
      background: '#ffffff',
      secondary: '#f5f7fa',
      textPrimary: '#303133',
      textSecondary: '#606266'
    },
    dark: {
      primary: '#00b96b',
      background: '#1a1a1a',
      secondary: '#242424',
      textPrimary: '#ffffff',
      textSecondary: '#909399'
    }
  }
  
  function toggleTheme() {
    isDark.value = !isDark.value
    updateTheme()
  }
  
  function updateTheme() {
    const theme = isDark.value ? themes.dark : themes.light
    
    document.documentElement.style.setProperty('--primary-color', theme.primary)
    document.documentElement.style.setProperty('--dark-bg', theme.background)
    document.documentElement.style.setProperty('--dark-secondary', theme.secondary)
    document.documentElement.style.setProperty('--text-light', theme.textPrimary)
    document.documentElement.style.setProperty('--text-gray', theme.textSecondary)
    
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
  }
  
  // 初始化主题
  updateTheme()
  
  return {
    isDark,
    toggleTheme
  }
}) 
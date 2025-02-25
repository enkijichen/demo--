import { defineStore } from 'pinia'
import { getUserInfo } from '@/api'
import type { User } from '@/types'
import { UserRole } from '@/types'
import api from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null as User | null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === UserRole.ADMIN,
    username: (state) => state.user?.username
  },

  actions: {
    async fetchUserInfo() {
      if (!this.token) return null
      
      try {
        const response = await getUserInfo()
        if (response.data.success) {
          this.user = response.data.data
          return this.user
        }
        return null
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        this.logout()
        return null
      }
    },

    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    }
  }
}) 
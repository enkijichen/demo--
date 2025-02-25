import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import { useUserStore } from '@/store/user'
import { UserRole } from '@/types'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '@/api'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/RegisterView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/app',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: { name: 'Orders' }
      },
      {
        path: 'textbooks',
        name: 'Textbooks',
        component: () => import('@/views/textbook/TextbookList.vue'),
        meta: { 
          requiresAuth: true,
          requiresUser: true
        }
      },
      {
        path: 'manage',
        name: 'Manage',
        component: () => import('@/views/manage/TextbookManage.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true,
          keepAlive: true
        }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { 
          requiresAuth: true,
          requiresUser: true
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'order-manage',
        name: 'OrderManage',
        component: () => import('@/views/manage/OrderManage.vue'),
        meta: { 
          requiresAuth: true,
          requiresAdmin: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      ElMessage.error('请先登录')
      next({ path: '/login' })
      return
    }

    // 如果没有用户信息，从token中解析
    if (!userStore.user) {
      try {
        const tokenPayload = JSON.parse(atob(token.split('.')[1]))
        userStore.user = {
          username: tokenPayload.sub,
          role: tokenPayload.role
        }
        
        // 检查管理员权限
        if (to.matched.some(record => record.meta.requiresAdmin)) {
          if (tokenPayload.role !== 'ADMIN') {
            ElMessage.error('无权访问管理员页面')
            next('/app/orders')
            return
          }
        }

        // 检查普通用户权限
        if (to.matched.some(record => record.meta.requiresUser)) {
          if (tokenPayload.role === 'ADMIN') {
            ElMessage.warning('您正在以管理员身份访问用户页面')
          }
        }

        // 根据角色重定向到对应的首页
        if (to.path === '/app' || to.path === '/app/') {
          if (tokenPayload.role === 'ADMIN') {
            next('/app/manage')
          } else {
            next('/app/textbooks')
          }
          return
        }

        next()
      } catch (error) {
        console.error('Failed to parse token:', error)
        ElMessage.error('登录状态异常，请重新登录')
        next('/login')
      }
    } else {
      // 已有用户信息，直接进行权限检查
      if (to.matched.some(record => record.meta.requiresAdmin)) {
        if (userStore.user.role !== 'ADMIN') {
          ElMessage.error('无权访问管理员页面')
          next('/app/orders')
          return
        }
      }

      if (to.matched.some(record => record.meta.requiresUser)) {
        if (userStore.user.role === 'ADMIN') {
          ElMessage.warning('您正在以管理员身份访问用户页面')
        }
      }

      // 根据角色重定向到对应的首页
      if (to.path === '/app' || to.path === '/app/') {
        if (userStore.user.role === 'ADMIN') {
          next('/app/manage')
        } else {
          next('/app/textbooks')
        }
        return
      }

      next()
    }
  } else if (to.path === '/login' && token) {
    // 已登录用户访问登录页面时重定向到对应的首页
    const tokenPayload = JSON.parse(atob(token.split('.')[1]))
    if (tokenPayload.role === 'ADMIN') {
      next('/app/manage')
    } else {
      next('/app/textbooks')
    }
  } else {
    next()
  }
})

export default router

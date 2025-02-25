import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import config from '@/config'
import { useUserStore } from '@/store/user'
import type { ApiResponse, LoginForm, RegisterForm, User, Order } from '@/types'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('Request with token:', token)
      console.log('Request URL:', config.url)
      console.log('Request headers:', config.headers)
    } else {
      console.warn('No token found for request:', config.url)
    }
    return config
  },
  error => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('Response:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    return response
  },
  error => {
    console.error('Response error:', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    })
    
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

interface LoginResponse {
  success: boolean
  message: string
  data: string  // token
}

interface UserResponse {
  success: boolean
  message: string
  data: User
}

interface CreateOrderRequest {
  textbookId: number;
  quantity: number;
  receiver: string;
  phone: string;
  address: string;
  username: string;
}

interface TextbookParams {
  name?: string;
  grade?: string;
  subject?: string;
}

// 登录接口
export const login = (data: LoginForm) => {
  return api.post<ApiResponse<string>>('/auth/login', data)
}

export const getTextbooks = (params?: TextbookParams) => {
  return api.get<ApiResponse<Textbook[]>>('/textbooks', { 
    params,
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  })
}

export const createOrder = (data: CreateOrderRequest) => {
  return api.post<ApiResponse<Order>>('/orders', data)
}

export const getOrders = (username: string) => {
  return api.get<ApiResponse<Order[]>>('/orders/my-orders', { params: { username } })
}

export const updateOrderStatus = (orderId: number, status: string) => {
  return api.put<ApiResponse<Order>>(`/orders/${orderId}/status`, { status })
}

export const createTextbook = (data: Omit<Textbook, 'id'>) => {
  return api.post<ApiResponse<Textbook>>('/textbooks', data)
}

export const updateTextbook = (id: number, data: Partial<Textbook>) => {
  return api.put<ApiResponse<Textbook>>(`/textbooks/${id}`, data)
}

export const deleteTextbook = (id: number) => {
  return api.delete<ApiResponse<void>>(`/textbooks/${id}`)
}

// 注册接口
export const register = (data: RegisterRequest) => {
  return api.post<ApiResponse<User>>('/auth/register', {
    username: data.username,
    password: data.password,
    email: data.email,
    phone: data.phone,
    adminKey: data.adminKey
  })
}

// 获取用户信息接口
export const getUserInfo = (params: { username: string }) => {
  return api.get<ApiResponse<User>>('/auth/user/info', { params })
}

// 修改密码
export const updatePassword = (data: {
  oldPassword: string;
  newPassword: string;
}) => {
  const userStore = useUserStore()
  return api.put<ApiResponse<void>>('/users/password', {
    ...data,
    username: userStore.user?.username
  })
}

// 获取待处理订单
export const getPendingOrders = (username: string) => {
  return api.get<ApiResponse<Order[]>>('/orders/pending', { params: { username } })
}

export default api 

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
} 
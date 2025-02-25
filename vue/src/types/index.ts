export interface User {
  id: number
  username: string
  email: string
  phone: string
  role: UserRole
}

export interface Textbook {
  id: number
  name: string
  grade: string
  subject: string
  price: number
  stock: number
}

export interface TextbookWithQuantity extends Textbook {
  quantity: number
}

export interface Order {
  id: number
  user: User
  textbook: Textbook
  quantity: number
  status: string
  address: string
  createTime: string
  updateTime: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  email: string
  phone: string
  isAdmin: boolean
  adminKey?: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone: string
  adminKey?: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export interface CreateOrderRequest {
  textbookId: number
  quantity: number
  address: string
} 
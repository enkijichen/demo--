import { ElMessage } from 'element-plus'

export const handleError = (error: any, customMessage?: string) => {
  console.error(error)
  const message = customMessage || error.response?.data?.message || '操作失败'
  ElMessage.error(message)
} 
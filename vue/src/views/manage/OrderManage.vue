<template>
  <div class="order-manage-container">
    <el-card class="order-list">
      <template #header>
        <div class="header">
          <h2>订单管理</h2>
        </div>
      </template>

      <el-table :data="orders" stripe style="width: 100%">
        <el-table-column prop="id" label="订单编号" width="100" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            {{ row.user.username }}
          </template>
        </el-table-column>
        <el-table-column label="收货信息" min-width="200">
          <template #default="{ row }">
            <div class="delivery-info">
              <p class="receiver">{{ row.receiver }}</p>
              <p class="phone">{{ row.phone }}</p>
              <el-tooltip
                effect="dark"
                :content="row.address"
                placement="top"
                :show-after="500"
              >
                <p class="address">{{ row.address }}</p>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="教材信息" min-width="200">
          <template #default="{ row }">
            <div class="book-info">
              <p>{{ row.textbook.name }}</p>
              <p class="sub-info">{{ row.textbook.grade }} | {{ row.textbook.subject }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column label="总价" width="120" align="center">
          <template #default="{ row }">
            ¥{{ (row.textbook.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PAID'"
              type="primary"
              size="small"
              @click="handleProcess(row)"
            >
              开始处理
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="success"
              size="small"
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllOrders, updateOrderStatus } from '@/api'
import type { Order } from '@/types'
import { useUserStore } from '@/store/user'

const orders = ref<Order[]>([])
const userStore = useUserStore()
const loading = ref(false)

const loadOrders = async () => {
  try {
    if (!userStore.user?.username) {
      ElMessage.error('用户未登录')
      return
    }
    
    loading.value = true
    const response = await getAllOrders(userStore.user.username)
    
    if (response.data.success) {
      orders.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '待支付': 'warning',
    '已支付': 'primary',
    '待处理': 'warning',
    '已发货': 'primary',
    '已收货': 'success',
    '已拒绝': 'danger',
    '已撤回': 'info'
  }
  return statusMap[getStatusText(status)] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'UNPAID': '待支付',
    'PAID': '已支付',
    'PENDING': '待处理',
    'SHIPPED': '已发货',
    'RECEIVED': '已收货',
    'REJECTED': '已拒绝',
    'CANCELLED': '已撤回'
  }
  return statusMap[status] || status
}

const handleProcess = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定开始处理该订单吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await updateOrderStatus(order.id, 'PENDING')
    if (response.data.success) {
      ElMessage.success('订单已开始处理')
      await loadOrders()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error: any) {
    if (error === 'cancel') return
    console.error('操作失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleShip = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定发货吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await updateOrderStatus(order.id, 'SHIPPED')
    if (response.data.success) {
      ElMessage.success('订单已发货')
      await loadOrders()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error: any) {
    if (error === 'cancel') return
    console.error('操作失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleReject = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定拒绝该订单吗？', {
      title: '确认操作',
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    const response = await updateOrderStatus(order.id, 'REJECTED')
    if (response.data.success) {
      ElMessage.success('订单已拒绝')
      await loadOrders()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to reject order:', error)
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.order-manage-container {
  padding: 30px;
  width: 100%;

  // 定义暗色主题的颜色变量
  --dark-bg: #1a1a1a;
  --dark-secondary: #242424;
  --dark-hover: #2c2c2c;
  --border-color: #333;
  --text-light: #e5e5e5;
  --text-gray: #888;
  
  --primary-color: #409eff;
  --primary-hover: #66b1ff;
  --primary-rgb: 64, 158, 255;
  
  --success-color: #67c23a;
  --success-rgb: 103, 194, 58;
  
  --warning-color: #e6a23c;
  --warning-rgb: 230, 162, 60;
  
  --danger-color: #f56c6c;
  --danger-hover: #f78989;
  --danger-rgb: 245, 108, 108;
  
  --info-color: #909399;
  --info-rgb: 144, 147, 153;

  .order-list {
    background-color: var(--dark-secondary);
    border-color: var(--border-color);
    border-radius: 4px;
    padding: 20px;

    .header {
      margin-bottom: 30px;

      h2 {
        margin: 0;
        font-size: 24px;
        color: var(--text-light);
      }
    }

    :deep(.el-table) {
      background-color: var(--dark-secondary);
      --el-table-border-color: var(--border-color);
      --el-table-bg-color: var(--dark-secondary);
      --el-table-tr-bg-color: var(--dark-secondary);
      --el-table-header-bg-color: var(--dark-bg);
      --el-table-header-text-color: var(--text-light);
      --el-table-text-color: var(--text-light);
      
      th.el-table__cell {
        background-color: var(--dark-bg);
        color: var(--text-light);
        font-weight: 500;
        border-bottom: 1px solid var(--border-color);
      }

      td.el-table__cell {
        background-color: var(--dark-secondary);
        color: var(--text-light);
        border-bottom: 1px solid var(--border-color);
      }

      .el-table__inner-wrapper::before {
        display: none;
      }

      .el-button-group {
        .el-button {
          background-color: var(--dark-bg);
          border-color: var(--border-color);
          
          &:hover {
            background-color: var(--dark-hover);
          }
          
          &.el-button--primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            
            &:hover {
              background-color: var(--primary-hover);
              border-color: var(--primary-hover);
            }
          }
          
          &.el-button--danger {
            background-color: var(--danger-color);
            border-color: var(--danger-color);
            
            &:hover {
              background-color: var(--danger-hover);
              border-color: var(--danger-hover);
            }
          }
        }
      }

      .el-tag {
        background-color: var(--dark-bg);
        border-color: var(--border-color);
        color: var(--text-light);
        
        &.el-tag--warning {
          background-color: rgba(var(--warning-rgb), 0.1);
          border-color: var(--warning-color);
          color: var(--warning-color);
        }
        
        &.el-tag--success {
          background-color: rgba(var(--success-rgb), 0.1);
          border-color: var(--success-color);
          color: var(--success-color);
        }
        
        &.el-tag--danger {
          background-color: rgba(var(--danger-rgb), 0.1);
          border-color: var(--danger-color);
          color: var(--danger-color);
        }
        
        &.el-tag--info {
          background-color: rgba(var(--info-rgb), 0.1);
          border-color: var(--info-color);
          color: var(--info-color);
        }
        
        &.el-tag--primary {
          background-color: rgba(var(--primary-rgb), 0.1);
          border-color: var(--primary-color);
          color: var(--primary-color);
        }
      }

      .user-info,
      .book-info,
      .delivery-info {
        p {
          margin: 0;
          line-height: 1.5;

          &.sub-info {
            color: var(--text-gray);
            font-size: 14px;
          }

          &.address {
            color: var(--text-gray);
            font-size: 14px;
            word-break: break-all;
          }
        }
      }
      
      // 斑马纹样式
      tr.el-table__row--striped td {
        background-color: var(--dark-bg);
      }
      
      // 鼠标悬停效果
      tr.el-table__row:hover td {
        background-color: var(--dark-hover) !important;
      }
    }
  }
}
</style> 
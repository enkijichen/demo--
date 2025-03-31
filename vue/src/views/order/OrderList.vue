<template>
  <div class="order-list-container">
    <el-card class="order-list">
      <template #header>
        <div class="header">
          <h2>我的订单</h2>
        </div>
      </template>

      <el-table :data="orders" stripe style="width: 100%">
        <el-table-column prop="id" label="订单编号" width="100" />
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
        <el-table-column prop="quantity" label="数量" width="120" align="center" />
        <el-table-column label="总价" width="150" align="center">
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
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'UNPAID'"
              type="primary"
              size="small"
              @click="handlePay(row)"
            >
              去支付
            </el-button>
            <el-button
              v-if="row.status === 'UNPAID'"
              type="warning"
              size="small"
              @click="handleCancel(row)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="row.status === 'SHIPPED'"
              type="success"
              size="small"
              @click="handleConfirmReceive(row)"
            >
              确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 支付确认弹窗 -->
    <el-dialog
      v-model="payDialogVisible"
      title="确认支付"
      width="400px"
    >
      <div class="pay-dialog-content">
        <p>订单金额：¥{{ currentOrder?.textbook.price * currentOrder?.quantity }}</p>
        <p>支付方式：模拟支付</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPay" :loading="payLoading">
            确认支付
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineComponent } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, updateOrderStatus, payOrder } from '@/api'
import type { Order } from '@/types'
import { useUserStore } from '@/store/user'

defineComponent({
  name: 'OrderList'
})

const orders = ref<Order[]>([])
const userStore = useUserStore()
const loading = ref(false)
const payDialogVisible = ref(false)
const payLoading = ref(false)
const currentOrder = ref<Order | null>(null)

const loadOrders = async () => {
  try {
    if (!userStore.user?.username) {
      ElMessage.error('用户未登录')
      return
    }
    
    loading.value = true
    const response = await getOrders(userStore.user.username)
    
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
    'UNPAID': 'warning',
    'PAID': 'primary',
    'PENDING': 'warning',
    'SHIPPED': 'primary',
    'RECEIVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info'
  }
  return statusMap[status] || 'info'
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

const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要撤回订单吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await updateOrderStatus(order.id, 'CANCELLED')
    console.log('Cancel order response:', response)
    
    if (response.data.success) {
      ElMessage.success('订单已撤回')
      await loadOrders()
    } else {
      ElMessage.error(response.data.message || '撤回订单失败')
    }
  } catch (error: any) {
    if (error === 'cancel') {
      return
    }
    console.error('撤回订单失败:', error)
    ElMessage.error(error.response?.data?.message || '撤回订单失败，请稍后重试')
  }
}

const handleConfirmReceive = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await updateOrderStatus(order.id, 'RECEIVED')
    if (response.success) {
      ElMessage.success('已确认收货')
      await loadOrders()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handlePay = (order: Order) => {
  currentOrder.value = order
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!currentOrder.value) return
  
  try {
    payLoading.value = true
    const response = await payOrder(currentOrder.value.id)
    
    if (response.data.success) {
      ElMessage.success('支付成功')
      payDialogVisible.value = false
      await loadOrders()
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    payLoading.value = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.order-list-container {
  padding: 30px;
  width: 100%;

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

      &::before {
        display: none;
      }

      .delivery-info {
        p {
          margin: 0;
          line-height: 1.5;

          &.receiver {
            color: var(--text-light);
            font-weight: 500;
          }

          &.phone {
            color: var(--text-gray);
            font-size: 14px;
          }

          &.address {
            color: var(--text-gray);
            font-size: 14px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 200px;
            cursor: pointer;
          }
        }
      }

      .book-info {
        p {
          margin: 0;
          line-height: 1.5;

          &.sub-info {
            color: var(--text-gray);
            font-size: 14px;
          }
        }
      }
    }
  }
}

.pay-dialog-content {
  p {
    margin: 10px 0;
    font-size: 16px;
    
    &:first-child {
      color: var(--primary-color);
      font-weight: bold;
    }
  }
}
</style> 
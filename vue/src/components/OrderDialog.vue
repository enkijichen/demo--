<template>
  <el-dialog
    v-model="dialogVisible"
    title="创建订单"
    width="500px"
  >
    <el-form
      ref="formRef"
      :model="orderForm"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="教材名称">
        <span>{{ textbook?.name }}</span>
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number
          v-model="orderForm.quantity"
          :min="1"
          :max="textbook?.stock"
          size="small"
        />
      </el-form-item>
      <el-form-item label="收货地址" prop="address">
        <el-input
          v-model="orderForm.address"
          type="textarea"
          :rows="3"
          placeholder="请输入详细收货地址"
        />
      </el-form-item>
      <el-form-item label="总价">
        <span class="price">¥{{ totalPrice }}</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          确认订购
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { createOrder } from '@/api'
import type { Textbook } from '@/types'

const props = defineProps<{
  textbook: Textbook | null
}>()

const emit = defineEmits(['success', 'update:modelValue'])

const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()

const orderForm = ref({
  quantity: 1,
  address: ''
})

const rules = {
  quantity: [
    { required: true, message: '请输入数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '数量必须大于0', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 5, message: '地址长度不能小于5个字符', trigger: 'blur' }
  ]
}

const totalPrice = computed(() => {
  if (!props.textbook) return '0.00'
  return (props.textbook.price * orderForm.value.quantity).toFixed(2)
})

const handleConfirm = async () => {
  if (!formRef.value || !props.textbook) return
  
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await createOrder({
      textbookId: props.textbook.id,
      quantity: orderForm.value.quantity,
      address: orderForm.value.address
    })

    if (response.success) {
      ElMessage.success('订单创建成功')
      emit('success')
      dialogVisible.value = false
      orderForm.value = { quantity: 1, address: '' }
    } else {
      ElMessage.error(response.message || '创建订单失败')
    }
  } catch (error: any) {
    console.error('创建订单失败:', error)
    ElMessage.error(error.response?.data?.message || '创建订单失败')
  } finally {
    loading.value = false
  }
}

defineExpose({
  dialogVisible
})
</script>

<style scoped lang="scss">
.price {
  color: var(--primary-color);
  font-size: 18px;
  font-weight: 500;
}

:deep(.el-dialog) {
  background-color: var(--dark-secondary);
  
  .el-dialog__header {
    margin: 0;
    padding: 20px;
    border-bottom: 1px solid var(--border-color);
    
    .el-dialog__title {
      color: var(--text-light);
    }
  }
  
  .el-dialog__body {
    padding: 20px;
    color: var(--text-light);
  }
  
  .el-dialog__footer {
    padding: 20px;
    border-top: 1px solid var(--border-color);
  }
}
</style> 
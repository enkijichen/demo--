<template>
  <div v-if="error" class="error-boundary">
    <el-result
      icon="error"
      title="出错了"
      :sub-title="error.message"
    >
      <template #extra>
        <el-button type="primary" @click="handleReset">
          重试
        </el-button>
      </template>
    </el-result>
  </div>
  <slot v-else />
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'

const error = ref<Error | null>(null)

onErrorCaptured((err) => {
  error.value = err
  return false
})

const handleReset = () => {
  error.value = null
}
</script>

<style scoped lang="scss">
.error-boundary {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
</style> 
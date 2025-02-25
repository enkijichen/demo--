<template>
  <div class="manage-container">
    <div class="manage-header">
      <h2>教材管理</h2>
      <el-button type="primary" @click="handleAdd">添加教材</el-button>
    </div>

    <div class="table-container">
      <el-table :data="textbooks" border>
        <el-table-column prop="name" label="教材名称" min-width="200" />
        <el-table-column prop="grade" label="年级" width="150" align="center" />
        <el-table-column prop="subject" label="科目" width="150" align="center" />
        <el-table-column prop="price" label="价格" width="150" align="center">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="150" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="textbookForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="教材名称" prop="name">
          <el-input v-model="textbookForm.name" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="textbookForm.grade" placeholder="请选择年级" style="width: 100%">
            <el-option label="初一" value="初一" />
            <el-option label="初二" value="初二" />
            <el-option label="初三" value="初三" />
            <el-option label="高一" value="高一" />
            <el-option label="高二" value="高二" />
            <el-option label="高三" value="高三" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="textbookForm.subject" placeholder="请选择科目" style="width: 100%">
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="政治" value="政治" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="生物" value="生物" />
            <el-option label="化学" value="化学" />
            <el-option label="物理" value="物理" />
            <el-option label="体育" value="体育" />
            <el-option label="艺术" value="艺术" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="textbookForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="textbookForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineComponent } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getTextbooks, createTextbook, updateTextbook, deleteTextbook } from '@/api'
import type { Textbook } from '@/types'

defineComponent({
  name: 'TextbookManage'
})

const textbooks = ref<Textbook[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const loading = ref(false)
const formRef = ref<FormInstance>()

const textbookForm = ref({
  id: undefined as number | undefined,
  name: '',
  grade: '',
  subject: '',
  price: 0,
  stock: 0
})

const rules = {
  name: [{ required: true, message: '请输入教材名称', trigger: 'blur' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

// 获取教材列表
const fetchTextbooks = async () => {
  try {
    const { data } = await getTextbooks()
    if (data.success) {
      textbooks.value = data.data
    }
  } catch (error) {
    console.error('获取教材列表失败:', error)
    ElMessage.error('获取教材列表失败')
  }
}

// 添加教材
const handleAdd = () => {
  dialogTitle.value = '添加教材'
  textbookForm.value = {
    id: undefined,
    name: '',
    grade: '',
    subject: '',
    price: 0,
    stock: 0
  }
  dialogVisible.value = true
}

// 编辑教材
const handleEdit = (row: Textbook) => {
  dialogTitle.value = '编辑教材'
  textbookForm.value = { ...row }
  dialogVisible.value = true
}

// 删除教材
const handleDelete = async (row: Textbook) => {
  try {
    await ElMessageBox.confirm('确定要删除该教材吗？', '提示', {
      type: 'warning'
    })
    const { data } = await deleteTextbook(row.id)
    if (data.success) {
      ElMessage.success('删除成功')
      await fetchTextbooks()
    }
  } catch (error) {
    console.error('删除教材失败:', error)
    ElMessage.error('删除教材失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { id, ...textbookData } = textbookForm.value
        let result;
        
        if (id !== undefined) {
          result = await updateTextbook(id, textbookData);
        } else {
          result = await createTextbook(textbookData);
        }
        
        if (result.data.success) {
          ElMessage.success(textbookForm.value.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          await fetchTextbooks()
        }
      } catch (error) {
        console.error(textbookForm.value.id ? '更新教材失败:' : '添加教材失败:', error)
        ElMessage.error(textbookForm.value.id ? '更新教材失败' : '添加教材失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchTextbooks()
})
</script>

<style scoped lang="scss">
.manage-container {
  padding: 30px;
  width: 100%;

  .manage-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;

    h2 {
      margin: 0;
      font-size: 24px;
      color: var(--text-light);
    }
  }

  :deep(.el-table) {
    background-color: var(--dark-secondary);
    
    // 修改表格边框颜色
    --el-table-border-color: var(--border-color);
    
    // 修改表头样式
    th.el-table__cell {
      background-color: var(--dark-bg);
      color: var(--text-light);
      font-weight: 500;
      border-bottom: 1px solid var(--border-color);
    }

    // 修改单元格样式
    td.el-table__cell {
      background-color: var(--dark-secondary);
      color: var(--text-light);
      border-bottom: 1px solid var(--border-color);
    }

    // 去除表格底部边框
    &::before {
      display: none;
    }
  }

  // 修改对话框样式
  :deep(.el-dialog) {
    background-color: var(--dark-secondary);

    .el-dialog__header {
      color: var(--text-light);
    }

    .el-dialog__body {
      color: var(--text-light);
    }

    .el-form-item__label {
      color: var(--text-light);
    }

    .el-input__wrapper,
    .el-select .el-input__wrapper,
    .el-input-number {
      background-color: var(--dark-bg);
      box-shadow: none;
      border: 1px solid var(--border-color);

      .el-input__inner {
        color: var(--text-light);
      }
    }
  }
}
</style> 
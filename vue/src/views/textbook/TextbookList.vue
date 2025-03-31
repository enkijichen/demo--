<template>
  <div class="textbook-container">
    <div class="filter-section">
      <el-form :inline="true" :model="filters">
        <el-form-item label="教材名称">
          <el-input
            v-model="filters.name"
            placeholder="输入教材名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="年级">
          <el-select 
            v-model="filters.grade" 
            placeholder="选择年级" 
            clearable
            @clear="handleSearch"
          >
            <el-option label="初一" value="初一" />
            <el-option label="初二" value="初二" />
            <el-option label="初三" value="初三" />
            <el-option label="高一" value="高一" />
            <el-option label="高二" value="高二" />
            <el-option label="高三" value="高三" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select 
            v-model="filters.subject" 
            placeholder="选择科目" 
            clearable
            @clear="handleSearch"
          >
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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button link @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="textbook-list">
      <el-row :gutter="20">
        <el-col 
          v-for="book in textbooks" 
          :key="book.id" 
          :span="6"
          :sm="12"
          :xs="24"
        >
          <el-card class="textbook-card" :body-style="{ padding: '20px' }">
            <div class="book-info">
              <div class="book-image" @click="handlePreviewImage(book)">
                <el-image
                  :src="book.imageUrl || '/images/textbooks/default.png'"
                  fit="cover"
                  :preview-src-list="book.imageUrl ? [book.imageUrl] : []"
                >
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <h3>{{ book.name }}</h3>
              <p>年级：{{ book.grade }}</p>
              <p>科目：{{ book.subject }}</p>
              <p class="price">¥{{ book.price?.toFixed(2) }}</p>
              <div class="book-actions">
                <el-input-number 
                  v-model="book.quantity" 
                  :min="1" 
                  :max="book.stock" 
                  size="small"
                  @change="handleQuantityChange(book)"
                />
                <el-button 
                  v-if="!userStore.isAdmin"
                  type="primary" 
                  @click="handleOrder(book)" 
                  :disabled="!book.stock || !book.quantity"
                >
                  {{ book.stock ? '订购' : '无库存' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 添加地址输入对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="填写收货信息"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input
            v-model="addressForm.address"
            type="textarea"
            rows="3"
            placeholder="请输入详细收货地址"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitLoading">
            确认订购
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getTextbooks, createOrder } from '@/api'
import type { Textbook, TextbookWithQuantity } from '@/types'
import { useUserStore } from '@/store/user'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'

const textbooks = ref<TextbookWithQuantity[]>([])
const filters = ref({
  name: '',
  grade: '',
  subject: ''
})

const userStore = useUserStore()
const router = useRouter()

const dialogVisible = ref(false)
const submitLoading = ref(false)
const addressFormRef = ref<FormInstance>()
const currentBook = ref<TextbookWithQuantity | null>(null)

const addressForm = ref({
  receiver: '',
  phone: '',
  address: ''
})

const addressRules = {
  receiver: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ]
}

const loadTextbooks = async (retryCount = 3) => {
  try {
    if (!userStore.token) {
      ElMessage.error('用户未登录')
      router.push('/login')
      return
    }

    const params = Object.fromEntries(
      Object.entries(filters.value).filter(([_, value]) => value !== '')
    )
    
    const response = await getTextbooks(params)
    console.log('API Response:', response)
    
    if (response?.data?.success) {
      const booksData = response.data.data || []
      textbooks.value = booksData.map((book: Textbook) => ({
        ...book,
        quantity: 1
      }))
      console.log('Loaded textbooks:', textbooks.value)
    } else {
      throw new Error(response?.data?.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取教材列表失败:', error)
    
    if (retryCount > 0) {
      console.log(`Retrying... (${retryCount} attempts left)`)
      await new Promise(resolve => setTimeout(resolve, 1000))
      return loadTextbooks(retryCount - 1)
    }
    
    ElMessage.error('获取教材列表失败，请刷新页面重试')
    textbooks.value = []
  }
}

const handleSearch = () => {
  loadTextbooks()
}

const handleReset = () => {
  filters.value = {
    name: '',
    grade: '',
    subject: ''
  }
  loadTextbooks()
}

const handleQuantityChange = (book: TextbookWithQuantity) => {
  if (book.quantity && book.quantity > book.stock) {
    book.quantity = book.stock
  }
  if (book.quantity && book.quantity < 1) {
    book.quantity = 1
  }
}

const handleOrder = (book: TextbookWithQuantity) => {
  if (!book.quantity || book.quantity < 1) {
    ElMessage.warning('请选择有效的订购数量')
    return
  }
  
  if (book.quantity > book.stock) {
    ElMessage.warning('订购数量不能超过库存数量')
    return
  }

  currentBook.value = book
  dialogVisible.value = true
}

const submitOrder = async () => {
  if (!addressFormRef.value || !currentBook.value) return
  
  try {
    await addressFormRef.value.validate()
    submitLoading.value = true
    
    const username = userStore.user?.username
    if (!username) {
      ElMessage.error('用户未登录')
      router.push('/login')
      return
    }
    
    const response = await createOrder({
      textbookId: currentBook.value.id,
      quantity: currentBook.value.quantity,
      receiver: addressForm.value.receiver,
      phone: addressForm.value.phone,
      address: addressForm.value.address,
      username: username
    })
    
    if (response.data.success) {
      ElMessage.success('订购成功')
      dialogVisible.value = false
      addressForm.value = {
        receiver: '',
        phone: '',
        address: ''
      }
      await loadTextbooks()
    } else {
      ElMessage.error(response.data.message || '订购失败')
    }
  } catch (error) {
    console.error('订购失败:', error)
    ElMessage.error('订购失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

const handlePreviewImage = (book: TextbookWithQuantity) => {
  // Implementation of handlePreviewImage function
}

onMounted(async () => {
  console.log('Component mounted')
  await nextTick()
  loadTextbooks()
})
</script>

<style scoped lang="scss">
.textbook-container {
  padding: 30px;
  width: 100%;

  .filter-section {
    margin-bottom: 30px;
    padding: 20px;
    background-color: var(--dark-secondary);
    border-radius: 8px;

    :deep(.el-form--inline) {
      .el-form-item {
        margin-right: 20px;
        margin-bottom: 0;

        .el-input,
        .el-select {
          width: 160px;
        }
      }
    }
  }

  .textbook-list {
    margin-top: 20px;
  }

  .textbook-card {
    height: 100%;
    background-color: var(--dark-secondary);
    border: none;
    transition: transform 0.3s;

    &:hover {
      transform: translateY(-5px);
    }

    .book-info {
      .book-image {
        width: 100%;
        height: 200px;
        margin-bottom: 15px;
        cursor: pointer;
        border-radius: 4px;
        overflow: hidden;
        background-color: var(--dark-bg);

        .el-image {
          width: 100%;
          height: 100%;
          
          .image-placeholder {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--text-gray);
            font-size: 24px;
          }
        }
      }

      h3 {
        margin: 0 0 15px;
        font-size: 18px;
        color: var(--text-light);
      }

      p {
        margin: 10px 0;
        color: var(--text-light);
        
        &.price {
          font-size: 20px;
          color: var(--primary-color);
          margin: 15px 0;
        }
      }

      .book-actions {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-top: 20px;

        .el-input-number {
          width: 120px;
        }

        .el-button {
          flex: 1;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style> 
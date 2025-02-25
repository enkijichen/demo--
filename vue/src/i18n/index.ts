import { createI18n } from 'vue-i18n'

const messages = {
  zh: {
    common: {
      search: '查询',
      add: '添加',
      edit: '编辑',
      delete: '删除',
      confirm: '确定',
      cancel: '取消',
      success: '成功',
      error: '错误',
      loading: '加载中...'
    },
    login: {
      title: '教辅资料订购系统',
      username: '用户名',
      password: '密码',
      submit: '登录',
      usernamePlaceholder: '请输入用户名',
      passwordPlaceholder: '请输入密码'
    },
    textbook: {
      title: '教材列表',
      name: '教材名称',
      grade: '年级',
      subject: '科目',
      price: '价格',
      stock: '库存',
      quantity: '数量',
      order: '订购'
    },
    order: {
      title: '我的订单',
      status: {
        PENDING: '待处理',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
      },
      actions: {
        approve: '通过',
        reject: '拒绝'
      }
    }
  },
  en: {
    common: {
      search: 'Search',
      add: 'Add',
      edit: 'Edit',
      delete: 'Delete',
      confirm: 'Confirm',
      cancel: 'Cancel',
      success: 'Success',
      error: 'Error',
      loading: 'Loading...'
    },
    login: {
      title: 'Educational Materials Order System',
      username: 'Username',
      password: 'Password',
      submit: 'Login',
      usernamePlaceholder: 'Please input username',
      passwordPlaceholder: 'Please input password'
    },
    textbook: {
      title: 'Textbook List',
      name: 'Name',
      grade: 'Grade',
      subject: 'Subject',
      price: 'Price',
      stock: 'Stock',
      quantity: 'Quantity',
      order: 'Order'
    },
    order: {
      title: 'My Orders',
      status: {
        PENDING: 'Pending',
        APPROVED: 'Approved',
        REJECTED: 'Rejected'
      },
      actions: {
        approve: 'Approve',
        reject: 'Reject'
      }
    }
  }
}

export const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('locale') || 'zh',
  fallbackLocale: 'zh',
  messages
}) 
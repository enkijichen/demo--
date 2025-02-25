import { watch } from 'vue'
import { type PiniaPluginContext } from 'pinia'

export function createPersistedState(options?: {
  key?: string
  paths?: string[]
}) {
  return ({ store }: PiniaPluginContext) => {
    const key = options?.key || store.$id
    
    // 从 localStorage 恢复状态
    const fromStorage = localStorage.getItem(key)
    if (fromStorage) {
      store.$patch(JSON.parse(fromStorage))
    }
    
    // 监听状态变化并保存到 localStorage
    watch(
      () => store.$state,
      (state) => {
        const toStore = options?.paths
          ? options.paths.reduce((obj, path) => {
              obj[path] = state[path]
              return obj
            }, {} as Record<string, any>)
          : state
          
        localStorage.setItem(key, JSON.stringify(toStore))
      },
      { deep: true }
    )
  }
} 
import type { Plugin } from 'vue'
import { createPinia } from 'pinia'

export const piniaPlugin: Plugin = {
  install(app) {
    const pinia = createPinia()
    app.use(pinia)
  }
}

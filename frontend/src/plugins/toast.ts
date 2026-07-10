import type { Plugin } from 'vue'
import ToastService from 'primevue/toastservice'

export const toastPlugin: Plugin = {
  install(app) {
    app.use(ToastService)
  }
}

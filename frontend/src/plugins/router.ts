import type { Plugin } from 'vue'
import router from '@/router'

export const routerPlugin: Plugin = {
  install(app) {
    app.use(router)
  }
}

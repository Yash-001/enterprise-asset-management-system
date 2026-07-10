import type { Plugin } from 'vue'
import ConfirmationService from 'primevue/confirmationservice'

export const confirmPlugin: Plugin = {
  install(app) {
    app.use(ConfirmationService)
  }
}

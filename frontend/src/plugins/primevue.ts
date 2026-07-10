import type { Plugin } from 'vue'
import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'

export const primevuePlugin: Plugin = {
  install(app) {
    app.use(PrimeVue, {
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.app-dark'
        }
      },
      ripple: true
    })
  }
}

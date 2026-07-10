import type { App } from 'vue'
import { piniaPlugin } from './pinia'
import { routerPlugin } from './router'
import { primevuePlugin } from './primevue'
import { toastPlugin } from './toast'
import { confirmPlugin } from './confirm'
import { componentsPlugin } from './components'

/**
 * Registers all application plugins in the correct order.
 */
export function registerPlugins(app: App): void {
  app.use(piniaPlugin)
  app.use(routerPlugin)
  app.use(primevuePlugin)
  app.use(toastPlugin)
  app.use(confirmPlugin)
  app.use(componentsPlugin)
}

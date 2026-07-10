import type { Plugin } from 'vue'
import Tooltip from 'primevue/tooltip'

/**
 * Global component registration plugin.
 * Base components will be registered here as they are created in later chunks.
 */
export const componentsPlugin: Plugin = {
  install(app) {
    // Global directives
    app.directive('tooltip', Tooltip)

    // Global Base Components (will be added in Chunk 3)
    // Example:
    // app.component('BaseButton', BaseButton)
    // app.component('BaseCard', BaseCard)
  }
}

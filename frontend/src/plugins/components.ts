import type { Plugin } from 'vue'
import Tooltip from 'primevue/tooltip'

// Base Components
import BasePageHeader from '@/shared/components/BasePageHeader.vue'
import BaseCard from '@/shared/components/BaseCard.vue'
import BaseStatsCard from '@/shared/components/BaseStatsCard.vue'
import BaseDataTable from '@/shared/components/BaseDataTable.vue'
import BaseStatusChip from '@/shared/components/BaseStatusChip.vue'
import BaseEmptyState from '@/shared/components/BaseEmptyState.vue'
import BaseConfirmDialog from '@/shared/components/BaseConfirmDialog.vue'
import BaseDeleteDialog from '@/shared/components/BaseDeleteDialog.vue'
import BaseLoading from '@/shared/components/BaseLoading.vue'

/**
 * Global component and directive registration.
 */
export const componentsPlugin: Plugin = {
  install(app) {
    // Directives
    app.directive('tooltip', Tooltip)

    // Base Components — available everywhere without imports
    app.component('BasePageHeader', BasePageHeader)
    app.component('BaseCard', BaseCard)
    app.component('BaseStatsCard', BaseStatsCard)
    app.component('BaseDataTable', BaseDataTable)
    app.component('BaseStatusChip', BaseStatusChip)
    app.component('BaseEmptyState', BaseEmptyState)
    app.component('BaseConfirmDialog', BaseConfirmDialog)
    app.component('BaseDeleteDialog', BaseDeleteDialog)
    app.component('BaseLoading', BaseLoading)
  }
}

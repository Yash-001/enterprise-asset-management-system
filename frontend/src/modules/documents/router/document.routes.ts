import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const documentRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.DOCUMENTS,
    name: ROUTE_NAMES.DOCUMENTS,
    component: () => import('../views/DocumentListView.vue'),
    meta: {
      title: 'Documents',
      requiresAuth: true,
      permissions: [PERMISSIONS.DOCUMENT_VIEW],
      featureFlag: 'DOCUMENTS'
    }
  }
]

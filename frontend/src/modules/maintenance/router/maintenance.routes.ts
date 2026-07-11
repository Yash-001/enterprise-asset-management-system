import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const maintenanceRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.MAINTENANCE,
    name: ROUTE_NAMES.MAINTENANCE,
    component: () => import('../views/MaintenanceListView.vue'),
    meta: {
      title: 'Maintenance',
      requiresAuth: true,
      permissions: [PERMISSIONS.MAINTENANCE_VIEW]
    }
  }
]

import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const reportRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.REPORTS,
    name: ROUTE_NAMES.REPORTS,
    component: () => import('../views/ReportListView.vue'),
    meta: {
      title: 'Reports',
      requiresAuth: true,
      permissions: [PERMISSIONS.REPORT_VIEW],
      featureFlag: 'REPORTS'
    }
  }
]

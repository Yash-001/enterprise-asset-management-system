import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const workOrderRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.WORK_ORDERS,
    name: ROUTE_NAMES.WORK_ORDERS,
    component: () => import('../views/WorkOrderListView.vue'),
    meta: {
      title: 'Work Orders',
      requiresAuth: true,
      permissions: [PERMISSIONS.WORKORDER_VIEW]
    }
  }
]

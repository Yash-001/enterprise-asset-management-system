import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const purchaseOrderRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.PURCHASE_ORDERS,
    name: ROUTE_NAMES.PURCHASE_ORDERS,
    component: () => import('../views/PurchaseOrderListView.vue'),
    meta: {
      title: 'Purchase Orders',
      requiresAuth: true,
      permissions: [PERMISSIONS.PURCHASE_VIEW]
    }
  }
]

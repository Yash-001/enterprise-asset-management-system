import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const inventoryRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.INVENTORY,
    name: ROUTE_NAMES.INVENTORY,
    component: () => import('../views/InventoryListView.vue'),
    meta: {
      title: 'Inventory',
      requiresAuth: true,
      permissions: [PERMISSIONS.INVENTORY_VIEW]
    }
  }
]

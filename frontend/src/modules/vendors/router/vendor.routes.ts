import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const vendorRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.VENDORS,
    name: ROUTE_NAMES.VENDORS,
    component: () => import('../views/VendorListView.vue'),
    meta: {
      title: 'Vendors',
      requiresAuth: true,
      permissions: [PERMISSIONS.VENDOR_VIEW]
    }
  }
]

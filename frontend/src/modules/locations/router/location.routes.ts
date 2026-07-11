import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const locationRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.LOCATIONS,
    name: ROUTE_NAMES.LOCATIONS,
    component: () => import('../views/LocationListView.vue'),
    meta: {
      title: 'Locations',
      requiresAuth: true,
      permissions: [PERMISSIONS.LOCATION_VIEW]
    }
  }
]

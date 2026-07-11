import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const userRoutes: RouteRecordRaw = {
  path: ROUTE_PATHS.USERS,
  name: ROUTE_NAMES.USERS,
  component: () => import('../views/UserListView.vue'),
  meta: {
    title: 'Users',
    requiresAuth: true,
    permissions: [PERMISSIONS.USER_VIEW]
  }
}

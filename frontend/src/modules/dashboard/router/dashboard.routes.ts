import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS } from '@/shared/constants'

export const dashboardRoutes: RouteRecordRaw = {
  path: ROUTE_PATHS.DASHBOARD,
  name: ROUTE_NAMES.DASHBOARD,
  component: () => import('../views/DashboardView.vue'),
  meta: {
    title: 'Dashboard',
    requiresAuth: true
  }
}

import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const notificationRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.NOTIFICATIONS,
    name: ROUTE_NAMES.NOTIFICATIONS,
    component: () => import('../views/NotificationListView.vue'),
    meta: {
      title: 'Notifications',
      requiresAuth: true,
      permissions: [PERMISSIONS.NOTIFICATION_VIEW],
      featureFlag: 'NOTIFICATIONS'
    }
  }
]

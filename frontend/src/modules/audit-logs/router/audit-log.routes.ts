import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const auditLogRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.AUDIT_LOGS,
    name: ROUTE_NAMES.AUDIT_LOGS,
    component: () => import('../views/AuditLogListView.vue'),
    meta: {
      title: 'Audit Logs',
      requiresAuth: true,
      permissions: [PERMISSIONS.AUDIT_VIEW]
    }
  }
]

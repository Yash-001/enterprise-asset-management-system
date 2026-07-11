import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const assignmentRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.ASSIGNMENTS,
    name: ROUTE_NAMES.ASSIGNMENTS,
    component: () => import('../views/AssignmentListView.vue'),
    meta: {
      title: 'Assignments',
      requiresAuth: true,
      permissions: [PERMISSIONS.ASSIGNMENT_VIEW]
    }
  }
]

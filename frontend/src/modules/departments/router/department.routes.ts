import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const departmentRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.DEPARTMENTS,
    name: ROUTE_NAMES.DEPARTMENTS,
    component: () => import('../views/DepartmentListView.vue'),
    meta: {
      title: 'Departments',
      requiresAuth: true,
      permissions: [PERMISSIONS.DEPARTMENT_VIEW]
    }
  }
]

import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS } from '@/shared/constants'

export const authRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.LOGIN,
    name: ROUTE_NAMES.LOGIN,
    component: () => import('../views/LoginView.vue'),
    meta: {
      title: 'Login',
      requiresAuth: false,
      layout: 'auth'
    }
  },
  {
    path: ROUTE_PATHS.REGISTER,
    name: ROUTE_NAMES.REGISTER,
    component: () => import('../views/RegisterView.vue'),
    meta: {
      title: 'Register',
      requiresAuth: false,
      layout: 'auth'
    }
  }
]

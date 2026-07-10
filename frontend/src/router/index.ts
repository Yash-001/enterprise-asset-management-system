import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { authRoutes } from '@/modules/auth'
import { ROUTE_NAMES, ROUTE_PATHS } from '@/shared/constants'
import { authGuard } from './guards'

const routes: RouteRecordRaw[] = [
  // Auth routes (public)
  ...authRoutes,

  // Protected routes (AppLayout will wrap these in later chunks)
  {
    path: '/',
    redirect: ROUTE_PATHS.DASHBOARD
  },
  {
    path: ROUTE_PATHS.DASHBOARD,
    name: ROUTE_NAMES.DASHBOARD,
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { title: 'Dashboard', requiresAuth: true }
  },

  // Error pages
  {
    path: ROUTE_PATHS.FORBIDDEN,
    name: ROUTE_NAMES.FORBIDDEN,
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { title: 'Access Denied', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: ROUTE_NAMES.NOT_FOUND,
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { title: 'Page Not Found', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(authGuard)

export default router

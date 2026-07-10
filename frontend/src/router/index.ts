import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { title: 'Dashboard', requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/layouts/AppLayout.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

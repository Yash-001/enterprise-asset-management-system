import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { authRoutes } from '@/modules/auth'
import { dashboardRoutes } from '@/modules/dashboard'
import { userRoutes } from '@/modules/users'
import { assetRoutes } from '@/modules/assets'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'
import { isFeatureEnabled } from '@/shared/config'
import { authGuard } from './guards'

/**
 * Application-level routes.
 * Feature module routes are aggregated here.
 * Protected routes are wrapped with AppLayout (children pattern).
 */
const routes: RouteRecordRaw[] = [
  // ─── Auth (public) ────────────────────────────────────────────────────
  ...authRoutes,

  // ─── Protected (AppLayout wrapper) ────────────────────────────────────
  {
    path: '/',
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: ROUTE_PATHS.DASHBOARD
      },
      dashboardRoutes,

      // ─── Assets ───────────────────────────────────────────────────
      ...assetRoutes,

      // ─── Work Orders ──────────────────────────────────────────────
      {
        path: ROUTE_PATHS.WORK_ORDERS,
        name: ROUTE_NAMES.WORK_ORDERS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Work Orders',
          permissions: [PERMISSIONS.WORKORDER_VIEW]
        }
      },

      // ─── Maintenance ──────────────────────────────────────────────
      {
        path: ROUTE_PATHS.MAINTENANCE,
        name: ROUTE_NAMES.MAINTENANCE,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Maintenance',
          permissions: [PERMISSIONS.MAINTENANCE_VIEW]
        }
      },

      // ─── Inventory ────────────────────────────────────────────────
      {
        path: ROUTE_PATHS.INVENTORY,
        name: ROUTE_NAMES.INVENTORY,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Inventory',
          permissions: [PERMISSIONS.INVENTORY_VIEW]
        }
      },

      // ─── Purchase Orders ──────────────────────────────────────────
      {
        path: ROUTE_PATHS.PURCHASE_ORDERS,
        name: ROUTE_NAMES.PURCHASE_ORDERS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Purchase Orders',
          permissions: [PERMISSIONS.PURCHASE_VIEW]
        }
      },

      // ─── Vendors ──────────────────────────────────────────────────
      {
        path: ROUTE_PATHS.VENDORS,
        name: ROUTE_NAMES.VENDORS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Vendors',
          permissions: [PERMISSIONS.VENDOR_VIEW]
        }
      },

      // ─── Departments ──────────────────────────────────────────────
      {
        path: ROUTE_PATHS.DEPARTMENTS,
        name: ROUTE_NAMES.DEPARTMENTS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Departments',
          permissions: [PERMISSIONS.DEPARTMENT_VIEW]
        }
      },

      // ─── Locations ────────────────────────────────────────────────
      {
        path: ROUTE_PATHS.LOCATIONS,
        name: ROUTE_NAMES.LOCATIONS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Locations',
          permissions: [PERMISSIONS.LOCATION_VIEW]
        }
      },

      // ─── Users (Admin only) ───────────────────────────────────────
      userRoutes,

      // ─── Notifications (feature-flagged) ──────────────────────────
      ...(isFeatureEnabled('NOTIFICATIONS')
        ? [
            {
              path: ROUTE_PATHS.NOTIFICATIONS,
              name: ROUTE_NAMES.NOTIFICATIONS,
              component: () => import('@/layouts/AppLayout.vue'), // placeholder
              meta: {
                title: 'Notifications',
                permissions: [PERMISSIONS.NOTIFICATION_VIEW],
                featureFlag: 'NOTIFICATIONS' as const
              }
            }
          ]
        : []),

      // ─── Documents (feature-flagged) ──────────────────────────────
      ...(isFeatureEnabled('DOCUMENTS')
        ? [
            {
              path: ROUTE_PATHS.DOCUMENTS,
              name: ROUTE_NAMES.DOCUMENTS,
              component: () => import('@/layouts/AppLayout.vue'), // placeholder
              meta: {
                title: 'Documents',
                permissions: [PERMISSIONS.DOCUMENT_VIEW],
                featureFlag: 'DOCUMENTS' as const
              }
            }
          ]
        : []),

      // ─── Reports (feature-flagged) ────────────────────────────────
      ...(isFeatureEnabled('REPORTS')
        ? [
            {
              path: ROUTE_PATHS.REPORTS,
              name: ROUTE_NAMES.REPORTS,
              component: () => import('@/layouts/AppLayout.vue'), // placeholder
              meta: {
                title: 'Reports',
                permissions: [PERMISSIONS.REPORT_VIEW],
                featureFlag: 'REPORTS' as const
              }
            }
          ]
        : []),

      // ─── Audit Logs ───────────────────────────────────────────────
      {
        path: ROUTE_PATHS.AUDIT_LOGS,
        name: ROUTE_NAMES.AUDIT_LOGS,
        component: () => import('@/layouts/AppLayout.vue'), // placeholder
        meta: {
          title: 'Audit Logs',
          permissions: [PERMISSIONS.AUDIT_VIEW]
        }
      }
    ]
  },

  // ─── Error Pages ──────────────────────────────────────────────────────
  {
    path: ROUTE_PATHS.FORBIDDEN,
    name: ROUTE_NAMES.FORBIDDEN,
    component: () => import('@/views/ForbiddenView.vue'),
    meta: { title: 'Access Denied', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: ROUTE_NAMES.NOT_FOUND,
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: 'Page Not Found', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

router.beforeEach(authGuard)

export default router

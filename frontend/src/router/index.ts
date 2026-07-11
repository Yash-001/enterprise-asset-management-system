import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { authRoutes } from '@/modules/auth'
import { dashboardRoutes } from '@/modules/dashboard'
import { userRoutes } from '@/modules/users'
import { assetRoutes } from '@/modules/assets'
import { ROUTE_NAMES, ROUTE_PATHS } from '@/shared/constants'
import { isFeatureEnabled } from '@/shared/config'
import { authGuard } from './guards'

const routes: RouteRecordRaw[] = [
  // Auth (public)
  ...authRoutes,

  // Protected (AppLayout wrapper)
  {
    path: '/',
    component: () => import('@/layouts/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: ROUTE_PATHS.DASHBOARD },
      dashboardRoutes,
      ...assetRoutes,
      userRoutes,

      // Work Orders
      {
        path: ROUTE_PATHS.WORK_ORDERS,
        name: ROUTE_NAMES.WORK_ORDERS,
        component: () => import('@/modules/work-orders/views/WorkOrderListView.vue'),
        meta: { title: 'Work Orders', permissions: ['WORKORDER_VIEW'] }
      },

      // Assignments
      {
        path: ROUTE_PATHS.ASSIGNMENTS,
        name: ROUTE_NAMES.ASSIGNMENTS,
        component: () => import('@/modules/assignments/views/AssignmentListView.vue'),
        meta: { title: 'Assignments', permissions: ['ASSIGNMENT_VIEW'] }
      },

      // Maintenance
      {
        path: ROUTE_PATHS.MAINTENANCE,
        name: ROUTE_NAMES.MAINTENANCE,
        component: () => import('@/modules/maintenance/views/MaintenanceListView.vue'),
        meta: { title: 'Maintenance', permissions: ['MAINTENANCE_VIEW'] }
      },

      // Inventory
      {
        path: ROUTE_PATHS.INVENTORY,
        name: ROUTE_NAMES.INVENTORY,
        component: () => import('@/modules/inventory/views/InventoryListView.vue'),
        meta: { title: 'Inventory', permissions: ['INVENTORY_VIEW'] }
      },

      // Purchase Orders
      {
        path: ROUTE_PATHS.PURCHASE_ORDERS,
        name: ROUTE_NAMES.PURCHASE_ORDERS,
        component: () => import('@/modules/purchase-orders/views/PurchaseOrderListView.vue'),
        meta: { title: 'Purchase Orders', permissions: ['PURCHASE_VIEW'] }
      },

      // Vendors
      {
        path: ROUTE_PATHS.VENDORS,
        name: ROUTE_NAMES.VENDORS,
        component: () => import('@/modules/vendors/views/VendorListView.vue'),
        meta: { title: 'Vendors', permissions: ['VENDOR_VIEW'] }
      },

      // Departments
      {
        path: ROUTE_PATHS.DEPARTMENTS,
        name: ROUTE_NAMES.DEPARTMENTS,
        component: () => import('@/modules/departments/views/DepartmentListView.vue'),
        meta: { title: 'Departments', permissions: ['DEPARTMENT_VIEW'] }
      },

      // Locations
      {
        path: ROUTE_PATHS.LOCATIONS,
        name: ROUTE_NAMES.LOCATIONS,
        component: () => import('@/modules/locations/views/LocationListView.vue'),
        meta: { title: 'Locations', permissions: ['LOCATION_VIEW'] }
      },

      // Notifications (feature-flagged)
      ...(isFeatureEnabled('NOTIFICATIONS')
        ? [{
            path: ROUTE_PATHS.NOTIFICATIONS,
            name: ROUTE_NAMES.NOTIFICATIONS,
            component: () => import('@/modules/notifications/views/NotificationListView.vue'),
            meta: { title: 'Notifications', permissions: ['NOTIFICATION_VIEW'], featureFlag: 'NOTIFICATIONS' as const }
          }]
        : []),

      // Documents (feature-flagged)
      ...(isFeatureEnabled('DOCUMENTS')
        ? [{
            path: ROUTE_PATHS.DOCUMENTS,
            name: ROUTE_NAMES.DOCUMENTS,
            component: () => import('@/modules/documents/views/DocumentListView.vue'),
            meta: { title: 'Documents', permissions: ['DOCUMENT_VIEW'], featureFlag: 'DOCUMENTS' as const }
          }]
        : []),

      // Reports (feature-flagged)
      ...(isFeatureEnabled('REPORTS')
        ? [{
            path: ROUTE_PATHS.REPORTS,
            name: ROUTE_NAMES.REPORTS,
            component: () => import('@/modules/reports/views/ReportListView.vue'),
            meta: { title: 'Reports', permissions: ['REPORT_VIEW'], featureFlag: 'REPORTS' as const }
          }]
        : []),

      // Audit Logs
      {
        path: ROUTE_PATHS.AUDIT_LOGS,
        name: ROUTE_NAMES.AUDIT_LOGS,
        component: () => import('@/modules/audit-logs/views/AuditLogListView.vue'),
        meta: { title: 'Audit Logs', permissions: ['AUDIT_VIEW'] }
      }
    ]
  },

  // Error pages
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

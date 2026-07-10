/**
 * Backend API endpoint registry.
 * All URL paths are defined here — no hardcoded strings in services.
 */
export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/api/v1/auth/login',
    REGISTER: '/api/v1/auth/register'
  },
  USERS: {
    BASE: '/api/v1/users',
    BY_ID: (id: number) => `/api/v1/users/${id}`
  },
  ASSETS: {
    BASE: '/api/v1/assets',
    BY_ID: (id: number) => `/api/v1/assets/${id}`,
    SEARCH: '/api/v1/assets/search'
  },
  ASSIGNMENTS: {
    BASE: '/api/v1/asset-assignments',
    BY_ID: (id: number) => `/api/v1/asset-assignments/${id}`,
    RETURN: (id: number) => `/api/v1/asset-assignments/${id}/return`
  },
  WORK_ORDERS: {
    BASE: '/api/v1/work-orders',
    BY_ID: (id: number) => `/api/v1/work-orders/${id}`,
    SEARCH: '/api/v1/work-orders/search'
  },
  MAINTENANCE: {
    BASE: '/api/v1/maintenance-plans',
    BY_ID: (id: number) => `/api/v1/maintenance-plans/${id}`,
    SEARCH: '/api/v1/maintenance-plans/search',
    DASHBOARD: '/api/v1/maintenance-plans/dashboard',
    COMPLETE: (id: number) => `/api/v1/maintenance-plans/${id}/complete`
  },
  SPARE_PARTS: {
    BASE: '/api/v1/spare-parts',
    BY_ID: (id: number) => `/api/v1/spare-parts/${id}`,
    SEARCH: '/api/v1/spare-parts/search',
    LOW_STOCK: '/api/v1/spare-parts/low-stock',
    DASHBOARD: '/api/v1/spare-parts/dashboard'
  },
  STOCK_TRANSACTIONS: {
    BASE: '/api/v1/stock-transactions',
    BY_ID: (id: number) => `/api/v1/stock-transactions/${id}`,
    SEARCH: '/api/v1/stock-transactions/search'
  },
  PURCHASE_ORDERS: {
    BASE: '/api/v1/purchase-orders',
    BY_ID: (id: number) => `/api/v1/purchase-orders/${id}`,
    SEARCH: '/api/v1/purchase-orders/search'
  },
  VENDORS: {
    BASE: '/api/v1/vendors',
    BY_ID: (id: number) => `/api/v1/vendors/${id}`,
    SEARCH: '/api/v1/vendors/search'
  },
  DEPARTMENTS: {
    BASE: '/api/v1/departments',
    BY_ID: (id: number) => `/api/v1/departments/${id}`
  },
  LOCATIONS: {
    BASE: '/api/v1/locations',
    BY_ID: (id: number) => `/api/v1/locations/${id}`
  },
  NOTIFICATIONS: {
    BASE: '/api/v1/notifications',
    BY_ID: (id: number) => `/api/v1/notifications/${id}`,
    MARK_READ: (id: number) => `/api/v1/notifications/${id}/read`,
    MARK_ALL_READ: '/api/v1/notifications/read-all',
    UNREAD_COUNT: '/api/v1/notifications/unread-count'
  },
  DOCUMENTS: {
    BASE: '/api/v1/documents',
    BY_ID: (id: number) => `/api/v1/documents/${id}`,
    DOWNLOAD: (id: number) => `/api/v1/documents/${id}/download`,
    BY_REFERENCE: '/api/v1/documents/by-reference'
  },
  AUDIT_LOGS: {
    BASE: '/api/v1/audit-logs',
    ENTITY_HISTORY: (entity: string, id: number) => `/api/v1/audit-logs/entity/${entity}/${id}`
  },
  REPORTS: {
    BASE: '/api/v1/reports'
  },
  HEALTH: '/management/health'
} as const

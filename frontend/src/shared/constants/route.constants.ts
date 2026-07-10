export const ROUTE_NAMES = {
  // Auth
  LOGIN: 'login',
  REGISTER: 'register',

  // Main
  DASHBOARD: 'dashboard',

  // Assets
  ASSETS: 'assets',
  ASSET_CREATE: 'asset-create',
  ASSET_EDIT: 'asset-edit',
  ASSET_DETAIL: 'asset-detail',

  // Assignments
  ASSIGNMENTS: 'assignments',
  ASSIGNMENT_CREATE: 'assignment-create',

  // Work Orders
  WORK_ORDERS: 'work-orders',
  WORK_ORDER_CREATE: 'work-order-create',
  WORK_ORDER_EDIT: 'work-order-edit',
  WORK_ORDER_DETAIL: 'work-order-detail',

  // Maintenance
  MAINTENANCE: 'maintenance',
  MAINTENANCE_CREATE: 'maintenance-create',
  MAINTENANCE_EDIT: 'maintenance-edit',

  // Inventory
  INVENTORY: 'inventory',
  SPARE_PART_CREATE: 'spare-part-create',
  SPARE_PART_EDIT: 'spare-part-edit',
  STOCK_TRANSACTIONS: 'stock-transactions',

  // Purchase Orders
  PURCHASE_ORDERS: 'purchase-orders',
  PURCHASE_ORDER_CREATE: 'purchase-order-create',
  PURCHASE_ORDER_EDIT: 'purchase-order-edit',
  PURCHASE_ORDER_DETAIL: 'purchase-order-detail',

  // Vendors
  VENDORS: 'vendors',
  VENDOR_CREATE: 'vendor-create',
  VENDOR_EDIT: 'vendor-edit',

  // Reference Data
  DEPARTMENTS: 'departments',
  LOCATIONS: 'locations',
  USERS: 'users',

  // Notifications
  NOTIFICATIONS: 'notifications',

  // Documents
  DOCUMENTS: 'documents',

  // Audit & Reports
  AUDIT_LOGS: 'audit-logs',
  REPORTS: 'reports',

  // Error Pages
  FORBIDDEN: 'forbidden',
  NOT_FOUND: 'not-found'
} as const

export const ROUTE_PATHS = {
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  ASSETS: '/assets',
  ASSIGNMENTS: '/assignments',
  WORK_ORDERS: '/work-orders',
  MAINTENANCE: '/maintenance',
  INVENTORY: '/inventory',
  STOCK_TRANSACTIONS: '/inventory/transactions',
  PURCHASE_ORDERS: '/purchase-orders',
  VENDORS: '/vendors',
  DEPARTMENTS: '/departments',
  LOCATIONS: '/locations',
  USERS: '/users',
  NOTIFICATIONS: '/notifications',
  DOCUMENTS: '/documents',
  AUDIT_LOGS: '/audit-logs',
  REPORTS: '/reports',
  FORBIDDEN: '/403',
  NOT_FOUND: '/404'
} as const

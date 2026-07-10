import { ROUTE_NAMES } from '@/shared/constants'
import { PERMISSIONS, type Permission } from '@/shared/constants/permission.constants'

export interface NavItem {
  label: string
  icon: string
  routeName: string
  permissions: Permission[]
  featureFlag?: string
  children?: NavItem[]
}

export interface NavSection {
  title: string
  items: NavItem[]
}

export const navigationConfig: NavSection[] = [
  {
    title: 'Main',
    items: [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        routeName: ROUTE_NAMES.DASHBOARD,
        permissions: []
      }
    ]
  },
  {
    title: 'Asset Management',
    items: [
      {
        label: 'Assets',
        icon: 'pi pi-box',
        routeName: ROUTE_NAMES.ASSETS,
        permissions: [PERMISSIONS.ASSET_VIEW]
      },
      {
        label: 'Assignments',
        icon: 'pi pi-users',
        routeName: ROUTE_NAMES.ASSIGNMENTS,
        permissions: [PERMISSIONS.ASSIGNMENT_VIEW]
      }
    ]
  },
  {
    title: 'Operations',
    items: [
      {
        label: 'Work Orders',
        icon: 'pi pi-wrench',
        routeName: ROUTE_NAMES.WORK_ORDERS,
        permissions: [PERMISSIONS.WORKORDER_VIEW]
      },
      {
        label: 'Maintenance',
        icon: 'pi pi-calendar',
        routeName: ROUTE_NAMES.MAINTENANCE,
        permissions: [PERMISSIONS.MAINTENANCE_VIEW]
      }
    ]
  },
  {
    title: 'Supply Chain',
    items: [
      {
        label: 'Inventory',
        icon: 'pi pi-warehouse',
        routeName: ROUTE_NAMES.INVENTORY,
        permissions: [PERMISSIONS.INVENTORY_VIEW]
      },
      {
        label: 'Purchase Orders',
        icon: 'pi pi-shopping-cart',
        routeName: ROUTE_NAMES.PURCHASE_ORDERS,
        permissions: [PERMISSIONS.PURCHASE_VIEW]
      },
      {
        label: 'Vendors',
        icon: 'pi pi-building',
        routeName: ROUTE_NAMES.VENDORS,
        permissions: [PERMISSIONS.VENDOR_VIEW]
      }
    ]
  },
  {
    title: 'Organization',
    items: [
      {
        label: 'Departments',
        icon: 'pi pi-sitemap',
        routeName: ROUTE_NAMES.DEPARTMENTS,
        permissions: [PERMISSIONS.DEPARTMENT_VIEW]
      },
      {
        label: 'Locations',
        icon: 'pi pi-map-marker',
        routeName: ROUTE_NAMES.LOCATIONS,
        permissions: [PERMISSIONS.LOCATION_VIEW]
      },
      {
        label: 'Users',
        icon: 'pi pi-user',
        routeName: ROUTE_NAMES.USERS,
        permissions: [PERMISSIONS.USER_VIEW]
      }
    ]
  },
  {
    title: 'System',
    items: [
      {
        label: 'Notifications',
        icon: 'pi pi-bell',
        routeName: ROUTE_NAMES.NOTIFICATIONS,
        permissions: [PERMISSIONS.NOTIFICATION_VIEW],
        featureFlag: 'NOTIFICATIONS'
      },
      {
        label: 'Documents',
        icon: 'pi pi-file',
        routeName: ROUTE_NAMES.DOCUMENTS,
        permissions: [PERMISSIONS.DOCUMENT_VIEW],
        featureFlag: 'DOCUMENTS'
      },
      {
        label: 'Reports',
        icon: 'pi pi-chart-bar',
        routeName: ROUTE_NAMES.REPORTS,
        permissions: [PERMISSIONS.REPORT_VIEW],
        featureFlag: 'REPORTS'
      },
      {
        label: 'Audit Logs',
        icon: 'pi pi-history',
        routeName: ROUTE_NAMES.AUDIT_LOGS,
        permissions: [PERMISSIONS.AUDIT_VIEW]
      }
    ]
  }
]

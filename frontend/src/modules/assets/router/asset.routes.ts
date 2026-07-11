import type { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS, PERMISSIONS } from '@/shared/constants'

export const assetRoutes: RouteRecordRaw[] = [
  {
    path: ROUTE_PATHS.ASSETS,
    name: ROUTE_NAMES.ASSETS,
    component: () => import('../views/AssetListView.vue'),
    meta: {
      title: 'Assets',
      requiresAuth: true,
      permissions: [PERMISSIONS.ASSET_VIEW]
    }
  },
  {
    path: ROUTE_PATHS.ASSETS + '/new',
    name: ROUTE_NAMES.ASSET_CREATE,
    component: () => import('../views/AssetFormView.vue'),
    meta: {
      title: 'Create Asset',
      requiresAuth: true,
      permissions: [PERMISSIONS.ASSET_CREATE]
    }
  },
  {
    path: ROUTE_PATHS.ASSETS + '/:id/edit',
    name: ROUTE_NAMES.ASSET_EDIT,
    component: () => import('../views/AssetFormView.vue'),
    meta: {
      title: 'Edit Asset',
      requiresAuth: true,
      permissions: [PERMISSIONS.ASSET_EDIT]
    }
  }
]

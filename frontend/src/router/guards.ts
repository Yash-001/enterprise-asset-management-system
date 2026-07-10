import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '@/modules/auth'
import { ROUTE_NAMES } from '@/shared/constants'
import { ROLE_PERMISSIONS, type Permission } from '@/shared/constants/permission.constants'
import { isFeatureEnabled, type FeatureFlag } from '@/shared/config'
import { logger } from '@/shared/utils'

/**
 * Global navigation guard.
 * Handles: authentication, permission checks, feature flags, page title.
 */
export function authGuard(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext
): void {
  const authStore = useAuthStore()

  // ─── Set page title ───────────────────────────────────────────────────
  const title = to.meta.title as string | undefined
  document.title = title ? `${title} | EAMS` : 'EAMS'

  // ─── Public routes ────────────────────────────────────────────────────
  if (to.meta.requiresAuth === false) {
    if (authStore.isAuthenticated && (to.name === ROUTE_NAMES.LOGIN || to.name === ROUTE_NAMES.REGISTER)) {
      return next({ name: ROUTE_NAMES.DASHBOARD })
    }
    return next()
  }

  // ─── Authentication check ─────────────────────────────────────────────
  if (!authStore.isAuthenticated) {
    logger.info('Unauthenticated access attempt:', to.fullPath)
    return next({
      name: ROUTE_NAMES.LOGIN,
      query: { redirect: to.fullPath }
    })
  }

  // ─── Feature flag check ───────────────────────────────────────────────
  if (to.meta.featureFlag) {
    const flag = to.meta.featureFlag as FeatureFlag
    if (!isFeatureEnabled(flag)) {
      logger.warn(`Feature "${flag}" is disabled. Blocking route: ${to.path}`)
      return next({ name: ROUTE_NAMES.NOT_FOUND })
    }
  }

  // ─── Permission check ─────────────────────────────────────────────────
  if (to.meta.permissions && Array.isArray(to.meta.permissions)) {
    const requiredPermissions = to.meta.permissions as Permission[]
    const userRole = authStore.userRole

    if (!userRole) {
      return next({ name: ROUTE_NAMES.FORBIDDEN })
    }

    const userPermissions = ROLE_PERMISSIONS[userRole] || []
    const hasAccess = requiredPermissions.some((p) => userPermissions.includes(p))

    if (!hasAccess) {
      logger.warn(`Access denied to ${to.path}. Required: ${requiredPermissions.join(', ')}`)
      return next({ name: ROUTE_NAMES.FORBIDDEN })
    }
  }

  next()
}

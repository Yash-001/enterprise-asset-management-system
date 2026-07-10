import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '@/modules/auth'
import { ROUTE_NAMES } from '@/shared/constants'

/**
 * Global navigation guard — controls authentication and authorization.
 */
export function authGuard(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext
): void {
  const authStore = useAuthStore()

  // Public route — allow
  if (to.meta.requiresAuth === false) {
    // If already authenticated and going to login, redirect to dashboard
    if (authStore.isAuthenticated && to.name === ROUTE_NAMES.LOGIN) {
      return next({ name: ROUTE_NAMES.DASHBOARD })
    }
    return next()
  }

  // Protected route — check auth
  if (!authStore.isAuthenticated) {
    return next({
      name: ROUTE_NAMES.LOGIN,
      query: { redirect: to.fullPath }
    })
  }

  // Permission check (will be expanded in RBAC chunk)
  if (to.meta.permissions) {
    const requiredPermissions = to.meta.permissions as string[]
    const hasAccess = requiredPermissions.some((p) => {
      // Placeholder — full permission check comes with usePermission composable
      return true
    })
    if (!hasAccess) {
      return next({ name: ROUTE_NAMES.FORBIDDEN })
    }
  }

  next()
}

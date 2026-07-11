import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/modules/auth'
import { ROUTE_NAMES } from '@/shared/constants'
import type { UserRole } from '@/shared/types/user.types'

/**
 * Composable for quick auth access in components.
 *
 * Usage:
 *   const { isAuthenticated, user, isAdmin, logout } = useAuth()
 */
export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()

  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const user = computed(() => authStore.user)
  const userRole = computed(() => authStore.userRole)
  const fullName = computed(() => authStore.fullName)
  const isAdmin = computed(() => authStore.userRole === 'ADMIN')
  const isManager = computed(() => authStore.userRole === 'MANAGER')

  function hasRole(...roles: UserRole[]): boolean {
    return authStore.hasRole(...roles)
  }

  function logout(): void {
    authStore.logout()
    router.push({ name: ROUTE_NAMES.LOGIN })
  }

  return {
    isAuthenticated,
    user,
    userRole,
    fullName,
    isAdmin,
    isManager,
    hasRole,
    logout
  }
}

import { computed } from 'vue'
import { useAuthStore } from '@/modules/auth'
import { ROLE_PERMISSIONS, type Permission } from '@/shared/constants/permission.constants'

/**
 * Composable for permission checking in components.
 *
 * Usage:
 *   const { hasPermission, hasAnyPermission } = usePermission()
 *   v-if="hasPermission(PERMISSIONS.ASSET_CREATE)"
 */
export function usePermission() {
  const authStore = useAuthStore()

  const userPermissions = computed<Permission[]>(() => {
    const role = authStore.userRole
    return role ? ROLE_PERMISSIONS[role] || [] : []
  })

  function hasPermission(permission: Permission): boolean {
    return userPermissions.value.includes(permission)
  }

  function hasAnyPermission(permissions: Permission[]): boolean {
    return permissions.some((p) => userPermissions.value.includes(p))
  }

  function hasAllPermissions(permissions: Permission[]): boolean {
    return permissions.every((p) => userPermissions.value.includes(p))
  }

  return {
    userPermissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions
  }
}

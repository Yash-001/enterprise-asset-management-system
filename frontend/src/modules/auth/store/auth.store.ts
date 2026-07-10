import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { STORAGE_KEYS } from '@/shared/constants'
import { logger } from '@/shared/utils'
import { eventBus, EVENT_NAMES } from '@/shared/services'
import { useLoadingStore } from '@/shared/stores'
import { authService } from '../services'
import type { AuthenticatedUser, DecodedToken, LoginCredentials, RegisterPayload } from '../types'
import type { UserRole } from '@/shared/types/user.types'

/**
 * Auth store — manages JWT, user session, and authentication state.
 */
export const useAuthStore = defineStore('auth', () => {
  const loadingStore = useLoadingStore()

  // ─── State ────────────────────────────────────────────────────────────────
  const token = ref<string | null>(localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN))
  const user = ref<AuthenticatedUser | null>(restoreUser())

  // ─── Getters ──────────────────────────────────────────────────────────────
  const isAuthenticated = computed(() => !!token.value && !isTokenExpired())
  const userRole = computed<UserRole | null>(() => user.value?.role ?? null)
  const fullName = computed(() =>
    user.value ? `${user.value.firstName} ${user.value.lastName}` : ''
  )

  // ─── Actions ──────────────────────────────────────────────────────────────

  async function login(credentials: LoginCredentials): Promise<void> {
    loadingStore.startLoading()
    try {
      const response = await authService.login(credentials)
      setSession(response.accessToken)
      logger.info('Login successful', user.value?.email)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function register(payload: RegisterPayload): Promise<void> {
    loadingStore.startLoading()
    try {
      await authService.register(payload)
      logger.info('Registration successful', payload.email)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function logout(): void {
    clearSession()
    eventBus.emit(EVENT_NAMES.SESSION_EXPIRED)
    logger.info('User logged out')
  }

  function hasRole(...roles: UserRole[]): boolean {
    return user.value !== null && roles.includes(user.value.role)
  }

  // ─── Private Helpers ──────────────────────────────────────────────────────

  function setSession(accessToken: string): void {
    token.value = accessToken
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken)

    const decoded = decodeToken(accessToken)
    if (decoded) {
      user.value = {
        email: decoded.sub,
        role: decoded.role,
        firstName: decoded.firstName,
        lastName: decoded.lastName
      }
      localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user.value))
      localStorage.setItem(STORAGE_KEYS.TOKEN_EXPIRY, String(decoded.exp * 1000))
    }
  }

  function clearSession(): void {
    token.value = null
    user.value = null
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.USER)
    localStorage.removeItem(STORAGE_KEYS.TOKEN_EXPIRY)
  }

  function decodeToken(jwt: string): DecodedToken | null {
    try {
      const payload = jwt.split('.')[1]
      const decoded = JSON.parse(atob(payload))
      return decoded as DecodedToken
    } catch (e) {
      logger.error('Failed to decode JWT token', e)
      return null
    }
  }

  function isTokenExpired(): boolean {
    const expiry = localStorage.getItem(STORAGE_KEYS.TOKEN_EXPIRY)
    if (!expiry) return true
    return Date.now() > Number(expiry)
  }

  function restoreUser(): AuthenticatedUser | null {
    try {
      const stored = localStorage.getItem(STORAGE_KEYS.USER)
      if (!stored) return null
      return JSON.parse(stored) as AuthenticatedUser
    } catch {
      return null
    }
  }

  function $reset(): void {
    clearSession()
  }

  return {
    token,
    user,
    isAuthenticated,
    userRole,
    fullName,
    login,
    register,
    logout,
    hasRole,
    $reset
  }
})

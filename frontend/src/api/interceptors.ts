import type { AxiosError, InternalAxiosRequestConfig } from 'axios'
import axiosInstance from './axios'
import { STORAGE_KEYS, API_CONSTANTS } from '@/shared/constants'
import { eventBus } from '@/shared/services'
import { logger } from '@/shared/utils'

/**
 * Setup request and response interceptors on the Axios instance.
 * Call once during app initialization.
 */
export function setupInterceptors(): void {
  // ─── Request Interceptor ────────────────────────────────────────────────
  axiosInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
      if (token && config.headers) {
        config.headers[API_CONSTANTS.HEADERS.AUTHORIZATION] = `Bearer ${token}`
      }
      return config
    },
    (error: AxiosError) => {
      logger.error('Request interceptor error:', error.message)
      return Promise.reject(error)
    }
  )

  // ─── Response Interceptor ───────────────────────────────────────────────
  axiosInstance.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      const status = error.response?.status

      switch (status) {
        case API_CONSTANTS.STATUS.UNAUTHORIZED:
          logger.warn('401 Unauthorized — session expired')
          localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
          localStorage.removeItem(STORAGE_KEYS.USER)
          localStorage.removeItem(STORAGE_KEYS.TOKEN_EXPIRY)
          eventBus.emit('SESSION_EXPIRED')
          break

        case API_CONSTANTS.STATUS.FORBIDDEN:
          logger.warn('403 Forbidden — insufficient permissions')
          break

        case API_CONSTANTS.STATUS.INTERNAL_ERROR:
          logger.error('500 Internal Server Error:', error.message)
          break

        default:
          break
      }

      return Promise.reject(error)
    }
  )
}

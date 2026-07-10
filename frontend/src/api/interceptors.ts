import apiClient from './axios'
import { STORAGE_KEYS } from '@/shared/constants/storage.constants'

/**
 * Setup Axios interceptors for auth and error handling.
 */
export function setupInterceptors(): void {
  // Request: Attach JWT
  apiClient.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  // Response: Global error handling
  apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error.response?.status

      if (status === 401) {
        localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
        localStorage.removeItem(STORAGE_KEYS.USER)
        window.location.href = '/login'
      }

      return Promise.reject(error)
    }
  )
}

import axios from 'axios'
import type { AxiosInstance } from 'axios'
import { API_CONSTANTS } from '@/shared/constants'

/**
 * Configured Axios instance — single source of truth for HTTP communication.
 * All requests flow through this instance (via ApiClient).
 */
const axiosInstance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: API_CONSTANTS.TIMEOUT.DEFAULT,
  headers: {
    [API_CONSTANTS.HEADERS.CONTENT_TYPE]: API_CONSTANTS.CONTENT_TYPE.JSON,
    [API_CONSTANTS.HEADERS.ACCEPT]: API_CONSTANTS.CONTENT_TYPE.JSON
  }
})

export default axiosInstance

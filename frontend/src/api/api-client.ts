import type { AxiosRequestConfig, AxiosResponse } from 'axios'
import axiosInstance from './axios'
import type { PageResponse } from '@/shared/types'

/**
 * Generic typed API client.
 * All feature services use this class — they never import Axios directly.
 */
export class ApiClient {
  /**
   * GET request returning typed data.
   */
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.get(url, config)
    return response.data
  }

  /**
   * GET request returning paginated data.
   */
  async getPaged<T>(url: string, params?: Record<string, unknown>): Promise<PageResponse<T>> {
    const response: AxiosResponse<PageResponse<T>> = await axiosInstance.get(url, { params })
    return response.data
  }

  /**
   * POST request with body, returning typed data.
   */
  async post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.post(url, data, config)
    return response.data
  }

  /**
   * PUT request with body, returning typed data.
   */
  async put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.put(url, data, config)
    return response.data
  }

  /**
   * PATCH request with body, returning typed data.
   */
  async patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.patch(url, data, config)
    return response.data
  }

  /**
   * DELETE request. Returns void by default.
   */
  async delete<T = void>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.delete(url, config)
    return response.data
  }

  /**
   * POST multipart/form-data (file upload).
   */
  async upload<T>(url: string, formData: FormData, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await axiosInstance.post(url, formData, {
      ...config,
      headers: {
        ...config?.headers,
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  }

  /**
   * GET file download — returns Blob.
   */
  async download(url: string, config?: AxiosRequestConfig): Promise<Blob> {
    const response: AxiosResponse<Blob> = await axiosInstance.get(url, {
      ...config,
      responseType: 'blob'
    })
    return response.data
  }
}

/**
 * Singleton ApiClient instance.
 */
export const apiClient = new ApiClient()

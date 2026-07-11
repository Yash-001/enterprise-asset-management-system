import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { VendorListItem, VendorCreatePayload, VendorSearchFilters } from '../types'

export class VendorService {
  async getAll(params: VendorSearchFilters = {}): Promise<PageResponse<VendorListItem>> {
    return apiClient.getPaged<VendorListItem>(ENDPOINTS.VENDORS.BASE, params as Record<string, unknown>)
  }

  async search(params: VendorSearchFilters): Promise<PageResponse<VendorListItem>> {
    return apiClient.getPaged<VendorListItem>(ENDPOINTS.VENDORS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<VendorListItem> {
    return apiClient.get<VendorListItem>(ENDPOINTS.VENDORS.BY_ID(id))
  }

  async create(payload: VendorCreatePayload): Promise<VendorListItem> {
    return apiClient.post<VendorListItem>(ENDPOINTS.VENDORS.BASE, payload)
  }

  async update(id: number, payload: Partial<VendorCreatePayload>): Promise<VendorListItem> {
    return apiClient.put<VendorListItem>(ENDPOINTS.VENDORS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.VENDORS.BY_ID(id))
  }
}

export const vendorService = new VendorService()

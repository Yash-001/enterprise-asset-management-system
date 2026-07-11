import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { SparePartListItem, SparePartCreatePayload, SparePartSearchFilters } from '../types'

export class InventoryService {
  async getAll(params: SparePartSearchFilters = {}): Promise<PageResponse<SparePartListItem>> {
    return apiClient.getPaged<SparePartListItem>(ENDPOINTS.SPARE_PARTS.BASE, params as Record<string, unknown>)
  }

  async search(params: SparePartSearchFilters): Promise<PageResponse<SparePartListItem>> {
    return apiClient.getPaged<SparePartListItem>(ENDPOINTS.SPARE_PARTS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<SparePartListItem> {
    return apiClient.get<SparePartListItem>(ENDPOINTS.SPARE_PARTS.BY_ID(id))
  }

  async create(payload: SparePartCreatePayload): Promise<SparePartListItem> {
    return apiClient.post<SparePartListItem>(ENDPOINTS.SPARE_PARTS.BASE, payload)
  }

  async update(id: number, payload: Partial<SparePartCreatePayload>): Promise<SparePartListItem> {
    return apiClient.put<SparePartListItem>(ENDPOINTS.SPARE_PARTS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.SPARE_PARTS.BY_ID(id))
  }

  async getLowStock(): Promise<SparePartListItem[]> {
    return apiClient.get<SparePartListItem[]>(ENDPOINTS.SPARE_PARTS.LOW_STOCK)
  }

  async getDashboard(): Promise<unknown> {
    return apiClient.get(ENDPOINTS.SPARE_PARTS.DASHBOARD)
  }
}

export const inventoryService = new InventoryService()

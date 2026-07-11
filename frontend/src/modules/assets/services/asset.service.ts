import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { AssetListItem, AssetCreatePayload, AssetUpdatePayload, AssetSearchFilters } from '../types'

export class AssetService {
  async getAll(params: AssetSearchFilters = {}): Promise<PageResponse<AssetListItem>> {
    return apiClient.getPaged<AssetListItem>(ENDPOINTS.ASSETS.BASE, params as Record<string, unknown>)
  }

  async search(params: AssetSearchFilters): Promise<PageResponse<AssetListItem>> {
    return apiClient.getPaged<AssetListItem>(ENDPOINTS.ASSETS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<AssetListItem> {
    return apiClient.get<AssetListItem>(ENDPOINTS.ASSETS.BY_ID(id))
  }

  async create(payload: AssetCreatePayload): Promise<AssetListItem> {
    return apiClient.post<AssetListItem>(ENDPOINTS.ASSETS.BASE, payload)
  }

  async update(id: number, payload: AssetUpdatePayload): Promise<AssetListItem> {
    return apiClient.put<AssetListItem>(ENDPOINTS.ASSETS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.ASSETS.BY_ID(id))
  }
}

export const assetService = new AssetService()

import { apiClient, ENDPOINTS } from '@/api'
import type { LocationListItem, LocationCreatePayload } from '../types'

export class LocationService {
  async getAll(): Promise<LocationListItem[]> {
    return apiClient.get<LocationListItem[]>(ENDPOINTS.LOCATIONS.BASE)
  }

  async getById(id: number): Promise<LocationListItem> {
    return apiClient.get<LocationListItem>(ENDPOINTS.LOCATIONS.BY_ID(id))
  }

  async create(payload: LocationCreatePayload): Promise<LocationListItem> {
    return apiClient.post<LocationListItem>(ENDPOINTS.LOCATIONS.BASE, payload)
  }

  async update(id: number, payload: Partial<LocationCreatePayload>): Promise<LocationListItem> {
    return apiClient.put<LocationListItem>(ENDPOINTS.LOCATIONS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.LOCATIONS.BY_ID(id))
  }
}

export const locationService = new LocationService()

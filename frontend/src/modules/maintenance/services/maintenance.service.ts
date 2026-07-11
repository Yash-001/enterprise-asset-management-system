import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { MaintenancePlanListItem, MaintenancePlanCreatePayload, MaintenanceSearchFilters } from '../types'

export class MaintenanceService {
  async getAll(params: MaintenanceSearchFilters = {}): Promise<PageResponse<MaintenancePlanListItem>> {
    return apiClient.getPaged<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.BASE, params as Record<string, unknown>)
  }

  async search(params: MaintenanceSearchFilters): Promise<PageResponse<MaintenancePlanListItem>> {
    return apiClient.getPaged<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<MaintenancePlanListItem> {
    return apiClient.get<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.BY_ID(id))
  }

  async create(payload: MaintenancePlanCreatePayload): Promise<MaintenancePlanListItem> {
    return apiClient.post<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.BASE, payload)
  }

  async update(id: number, payload: Partial<MaintenancePlanCreatePayload>): Promise<MaintenancePlanListItem> {
    return apiClient.put<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.MAINTENANCE.BY_ID(id))
  }

  async complete(id: number, completionDate?: string): Promise<MaintenancePlanListItem> {
    const params = completionDate ? { completionDate } : {}
    return apiClient.post<MaintenancePlanListItem>(ENDPOINTS.MAINTENANCE.COMPLETE(id), null, { params })
  }

  async getDashboard(): Promise<unknown> {
    return apiClient.get(ENDPOINTS.MAINTENANCE.DASHBOARD)
  }
}

export const maintenanceService = new MaintenanceService()

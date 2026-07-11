import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { WorkOrderListItem, WorkOrderCreatePayload, WorkOrderSearchFilters } from '../types'

export class WorkOrderService {
  async getAll(params: WorkOrderSearchFilters = {}): Promise<PageResponse<WorkOrderListItem>> {
    return apiClient.getPaged<WorkOrderListItem>(ENDPOINTS.WORK_ORDERS.BASE, params as Record<string, unknown>)
  }

  async search(params: WorkOrderSearchFilters): Promise<PageResponse<WorkOrderListItem>> {
    return apiClient.getPaged<WorkOrderListItem>(ENDPOINTS.WORK_ORDERS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<WorkOrderListItem> {
    return apiClient.get<WorkOrderListItem>(ENDPOINTS.WORK_ORDERS.BY_ID(id))
  }

  async create(payload: WorkOrderCreatePayload): Promise<WorkOrderListItem> {
    return apiClient.post<WorkOrderListItem>(ENDPOINTS.WORK_ORDERS.BASE, payload)
  }

  async update(id: number, payload: Partial<WorkOrderCreatePayload>): Promise<WorkOrderListItem> {
    return apiClient.put<WorkOrderListItem>(ENDPOINTS.WORK_ORDERS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.WORK_ORDERS.BY_ID(id))
  }
}

export const workOrderService = new WorkOrderService()

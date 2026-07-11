import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { PurchaseOrderListItem, PurchaseOrderCreatePayload, PurchaseOrderSearchFilters } from '../types'

export class PurchaseOrderService {
  async getAll(params: PurchaseOrderSearchFilters = {}): Promise<PageResponse<PurchaseOrderListItem>> {
    return apiClient.getPaged<PurchaseOrderListItem>(ENDPOINTS.PURCHASE_ORDERS.BASE, params as Record<string, unknown>)
  }

  async search(params: PurchaseOrderSearchFilters): Promise<PageResponse<PurchaseOrderListItem>> {
    return apiClient.getPaged<PurchaseOrderListItem>(ENDPOINTS.PURCHASE_ORDERS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<PurchaseOrderListItem> {
    return apiClient.get<PurchaseOrderListItem>(ENDPOINTS.PURCHASE_ORDERS.BY_ID(id))
  }

  async create(payload: PurchaseOrderCreatePayload): Promise<PurchaseOrderListItem> {
    return apiClient.post<PurchaseOrderListItem>(ENDPOINTS.PURCHASE_ORDERS.BASE, payload)
  }

  async update(id: number, payload: Partial<PurchaseOrderCreatePayload>): Promise<PurchaseOrderListItem> {
    return apiClient.put<PurchaseOrderListItem>(ENDPOINTS.PURCHASE_ORDERS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.PURCHASE_ORDERS.BY_ID(id))
  }
}

export const purchaseOrderService = new PurchaseOrderService()

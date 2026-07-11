import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { purchaseOrderService } from '../services'
import type { PurchaseOrderListItem, PurchaseOrderSearchFilters, PurchaseOrderCreatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const usePurchaseOrderStore = defineStore('purchase-orders', () => {
  const loadingStore = useLoadingStore()

  const orders = ref<PurchaseOrderListItem[]>([])
  const selectedOrder = ref<PurchaseOrderListItem | null>(null)
  const pagination = ref<PaginationState>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDirection: 'ASC',
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true
  })

  async function fetchOrders(filters: PurchaseOrderSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: PurchaseOrderSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await purchaseOrderService.search(params)
      orders.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      orders.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createOrder(payload: PurchaseOrderCreatePayload): Promise<PurchaseOrderListItem> {
    loadingStore.startLoading()
    try {
      return await purchaseOrderService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateOrder(id: number, payload: Partial<PurchaseOrderCreatePayload>): Promise<PurchaseOrderListItem> {
    loadingStore.startLoading()
    try {
      return await purchaseOrderService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteOrder(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await purchaseOrderService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    orders.value = []
    selectedOrder.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    orders,
    selectedOrder,
    pagination,
    fetchOrders,
    createOrder,
    updateOrder,
    deleteOrder,
    $reset
  }
})

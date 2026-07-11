import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { workOrderService } from '../services'
import type { WorkOrderListItem, WorkOrderSearchFilters, WorkOrderCreatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useWorkOrderStore = defineStore('work-orders', () => {
  const loadingStore = useLoadingStore()

  const workOrders = ref<WorkOrderListItem[]>([])
  const selectedWorkOrder = ref<WorkOrderListItem | null>(null)
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

  async function fetchWorkOrderById(id: number): Promise<WorkOrderListItem | null> {
    loadingStore.startLoading()
    try {
      selectedWorkOrder.value = await workOrderService.getById(id)
      return selectedWorkOrder.value
    } catch {
      return null
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function fetchWorkOrders(filters: WorkOrderSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: WorkOrderSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await workOrderService.search(params)
      workOrders.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      workOrders.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createWorkOrder(payload: WorkOrderCreatePayload): Promise<WorkOrderListItem> {
    loadingStore.startLoading()
    try {
      return await workOrderService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateWorkOrder(id: number, payload: Partial<WorkOrderCreatePayload>): Promise<WorkOrderListItem> {
    loadingStore.startLoading()
    try {
      return await workOrderService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteWorkOrder(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await workOrderService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    workOrders.value = []
    selectedWorkOrder.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    workOrders,
    selectedWorkOrder,
    pagination,
    fetchWorkOrderById,
    fetchWorkOrders,
    createWorkOrder,
    updateWorkOrder,
    deleteWorkOrder,
    $reset
  }
})

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { maintenanceService } from '../services'
import type { MaintenancePlanListItem, MaintenanceSearchFilters, MaintenancePlanCreatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useMaintenanceStore = defineStore('maintenance', () => {
  const loadingStore = useLoadingStore()

  const plans = ref<MaintenancePlanListItem[]>([])
  const selectedPlan = ref<MaintenancePlanListItem | null>(null)
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

  async function fetchPlans(filters: MaintenanceSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: MaintenanceSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await maintenanceService.search(params)
      plans.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      plans.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createPlan(payload: MaintenancePlanCreatePayload): Promise<MaintenancePlanListItem> {
    loadingStore.startLoading()
    try {
      return await maintenanceService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updatePlan(id: number, payload: Partial<MaintenancePlanCreatePayload>): Promise<MaintenancePlanListItem> {
    loadingStore.startLoading()
    try {
      return await maintenanceService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deletePlan(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await maintenanceService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    plans.value = []
    selectedPlan.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    plans,
    selectedPlan,
    pagination,
    fetchPlans,
    createPlan,
    updatePlan,
    deletePlan,
    $reset
  }
})

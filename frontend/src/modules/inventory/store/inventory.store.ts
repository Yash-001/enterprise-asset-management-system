import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { inventoryService } from '../services'
import type { SparePartListItem, SparePartSearchFilters, SparePartCreatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useInventoryStore = defineStore('inventory', () => {
  const loadingStore = useLoadingStore()

  const spareParts = ref<SparePartListItem[]>([])
  const selectedSparePart = ref<SparePartListItem | null>(null)
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

  async function fetchSpareParts(filters: SparePartSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: SparePartSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await inventoryService.search(params)
      spareParts.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      spareParts.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createSparePart(payload: SparePartCreatePayload): Promise<SparePartListItem> {
    loadingStore.startLoading()
    try {
      return await inventoryService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateSparePart(id: number, payload: Partial<SparePartCreatePayload>): Promise<SparePartListItem> {
    loadingStore.startLoading()
    try {
      return await inventoryService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteSparePart(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await inventoryService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    spareParts.value = []
    selectedSparePart.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    spareParts,
    selectedSparePart,
    pagination,
    fetchSpareParts,
    createSparePart,
    updateSparePart,
    deleteSparePart,
    $reset
  }
})

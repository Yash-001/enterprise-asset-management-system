import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { vendorService } from '../services'
import type { VendorListItem, VendorSearchFilters, VendorCreatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useVendorStore = defineStore('vendors', () => {
  const loadingStore = useLoadingStore()

  const vendors = ref<VendorListItem[]>([])
  const selectedVendor = ref<VendorListItem | null>(null)
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

  async function fetchVendors(filters: VendorSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: VendorSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await vendorService.search(params)
      vendors.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      vendors.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createVendor(payload: VendorCreatePayload): Promise<VendorListItem> {
    loadingStore.startLoading()
    try {
      return await vendorService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateVendor(id: number, payload: Partial<VendorCreatePayload>): Promise<VendorListItem> {
    loadingStore.startLoading()
    try {
      return await vendorService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteVendor(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await vendorService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    vendors.value = []
    selectedVendor.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    vendors,
    selectedVendor,
    pagination,
    fetchVendors,
    createVendor,
    updateVendor,
    deleteVendor,
    $reset
  }
})

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { assetService } from '../services'
import type { AssetListItem, AssetSearchFilters, AssetCreatePayload, AssetUpdatePayload } from '../types'
import type { PaginationState } from '@/shared/types'

export const useAssetStore = defineStore('assets', () => {
  const loadingStore = useLoadingStore()

  const assets = ref<AssetListItem[]>([])
  const selectedAsset = ref<AssetListItem | null>(null)
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

  async function fetchAssets(filters: AssetSearchFilters = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const params: AssetSearchFilters = {
        ...filters,
        page: filters.page ?? pagination.value.page,
        size: filters.size ?? pagination.value.size,
        sortBy: filters.sortBy ?? pagination.value.sortBy,
        sortDirection: filters.sortDirection ?? pagination.value.sortDirection
      }
      const response = await assetService.search(params)
      assets.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function fetchAssetById(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      selectedAsset.value = await assetService.getById(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createAsset(payload: AssetCreatePayload): Promise<AssetListItem> {
    loadingStore.startLoading()
    try {
      return await assetService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateAsset(id: number, payload: AssetUpdatePayload): Promise<AssetListItem> {
    loadingStore.startLoading()
    try {
      return await assetService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteAsset(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await assetService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    assets.value = []
    selectedAsset.value = null
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    assets,
    selectedAsset,
    pagination,
    fetchAssets,
    fetchAssetById,
    createAsset,
    updateAsset,
    deleteAsset,
    $reset
  }
})

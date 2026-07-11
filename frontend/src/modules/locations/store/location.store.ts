import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { locationService } from '../services'
import type { LocationListItem, LocationCreatePayload } from '../types'

export const useLocationStore = defineStore('locations', () => {
  const loadingStore = useLoadingStore()

  const locations = ref<LocationListItem[]>([])
  const selectedLocation = ref<LocationListItem | null>(null)

  async function fetchLocations(): Promise<void> {
    loadingStore.startLoading()
    try {
      locations.value = await locationService.getAll()
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function createLocation(payload: LocationCreatePayload): Promise<LocationListItem> {
    loadingStore.startLoading()
    try {
      return await locationService.create(payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function updateLocation(id: number, payload: Partial<LocationCreatePayload>): Promise<LocationListItem> {
    loadingStore.startLoading()
    try {
      return await locationService.update(id, payload)
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function deleteLocation(id: number): Promise<void> {
    loadingStore.startLoading()
    try {
      await locationService.delete(id)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    locations.value = []
    selectedLocation.value = null
  }

  return {
    locations,
    selectedLocation,
    fetchLocations,
    createLocation,
    updateLocation,
    deleteLocation,
    $reset
  }
})

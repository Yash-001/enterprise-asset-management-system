import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * Global loading state store.
 * Tracks concurrent request count — isLoading is true when any request is in flight.
 */
export const useLoadingStore = defineStore('loading', () => {
  const loadingCount = ref(0)

  const isLoading = computed(() => loadingCount.value > 0)

  function startLoading(): void {
    loadingCount.value++
  }

  function stopLoading(): void {
    if (loadingCount.value > 0) {
      loadingCount.value--
    }
  }

  function reset(): void {
    loadingCount.value = 0
  }

  return {
    loadingCount,
    isLoading,
    startLoading,
    stopLoading,
    reset
  }
})

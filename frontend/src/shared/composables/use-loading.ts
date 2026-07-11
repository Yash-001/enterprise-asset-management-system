import { ref } from 'vue'

/**
 * Composable for local loading state (per-action, per-view).
 * Use this for view-specific loading; use useLoadingStore for global.
 *
 * Usage:
 *   const { isLoading, withLoading } = useLoading()
 *   await withLoading(() => assetService.getAll())
 */
export function useLoading() {
  const isLoading = ref(false)

  async function withLoading<T>(fn: () => Promise<T>): Promise<T> {
    isLoading.value = true
    try {
      return await fn()
    } finally {
      isLoading.value = false
    }
  }

  function start(): void {
    isLoading.value = true
  }

  function stop(): void {
    isLoading.value = false
  }

  return {
    isLoading,
    withLoading,
    start,
    stop
  }
}

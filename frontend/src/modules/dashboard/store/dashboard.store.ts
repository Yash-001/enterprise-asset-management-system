import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { dashboardService } from '../services'
import type { DashboardStats } from '../types'

export const useDashboardStore = defineStore('dashboard', () => {
  const loadingStore = useLoadingStore()

  const stats = ref<DashboardStats>({
    totalAssets: 0,
    openWorkOrders: 0,
    overdueMaintenance: 0,
    lowStockItems: 0,
    pendingPurchaseOrders: 0,
    activeAssignments: 0
  })

  const isLoaded = ref(false)

  async function fetchStats(): Promise<void> {
    loadingStore.startLoading()
    try {
      stats.value = await dashboardService.getStats()
      isLoaded.value = true
    } catch {
      // Stats will remain at 0 — non-critical failure
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    stats.value = {
      totalAssets: 0,
      openWorkOrders: 0,
      overdueMaintenance: 0,
      lowStockItems: 0,
      pendingPurchaseOrders: 0,
      activeAssignments: 0
    }
    isLoaded.value = false
  }

  return {
    stats,
    isLoaded,
    fetchStats,
    $reset
  }
})

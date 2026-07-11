import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { reportService } from '../services'
import type { ReportFilter } from '../types'

export const useReportStore = defineStore('reports', () => {
  const loadingStore = useLoadingStore()

  const filters = ref<ReportFilter>({
    dateFrom: null,
    dateTo: null,
    category: null,
    department: null,
    status: null
  })

  const results = ref<unknown>(null)

  async function generateReport(): Promise<void> {
    loadingStore.startLoading()
    try {
      results.value = await reportService.generate(filters.value)
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    filters.value = {
      dateFrom: null,
      dateTo: null,
      category: null,
      department: null,
      status: null
    }
    results.value = null
  }

  return {
    filters,
    results,
    generateReport,
    $reset
  }
})

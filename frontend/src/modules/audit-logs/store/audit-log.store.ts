import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { auditLogService } from '../services'
import type { AuditLogResponse } from '../types'
import type { PaginationState } from '@/shared/types'

export const useAuditLogStore = defineStore('audit-logs', () => {
  const loadingStore = useLoadingStore()

  const auditLogs = ref<AuditLogResponse[]>([])
  const pagination = ref<PaginationState>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDirection: 'DESC',
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true
  })

  async function fetchAuditLogs(params: Record<string, unknown> = {}): Promise<void> {
    loadingStore.startLoading()
    try {
      const searchParams = {
        ...params,
        page: params.page ?? pagination.value.page,
        size: params.size ?? pagination.value.size,
        sortBy: params.sortBy ?? pagination.value.sortBy,
        sortDirection: params.sortDirection ?? pagination.value.sortDirection
      }
      const response = await auditLogService.search(searchParams)
      auditLogs.value = response.content
      pagination.value.totalElements = response.totalElements
      pagination.value.totalPages = response.totalPages
      pagination.value.first = response.first
      pagination.value.last = response.last
    } catch {
      // Silently handle — view stays rendered with empty data
      auditLogs.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  function $reset(): void {
    auditLogs.value = []
    pagination.value.page = 0
    pagination.value.totalElements = 0
  }

  return {
    auditLogs,
    pagination,
    fetchAuditLogs,
    $reset
  }
})

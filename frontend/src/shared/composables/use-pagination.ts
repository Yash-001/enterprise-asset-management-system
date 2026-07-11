import { reactive, computed } from 'vue'
import { APP_CONSTANTS } from '@/shared/constants'
import type { PaginationState, SortDirection, DataTablePageEvent, DataTableSortEvent } from '@/shared/types'

/**
 * Composable for managing server-side pagination, sorting, and page state.
 *
 * Usage:
 *   const { params, state, onPage, onSort, reset } = usePagination()
 *   await service.getAll(params.value)
 */
export function usePagination(defaults?: Partial<PaginationState>) {
  const state = reactive<PaginationState>({
    page: defaults?.page ?? 0,
    size: defaults?.size ?? APP_CONSTANTS.DEFAULT_PAGE_SIZE,
    sortBy: defaults?.sortBy ?? 'id',
    sortDirection: defaults?.sortDirection ?? 'ASC',
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true
  })

  const params = computed(() => ({
    page: state.page,
    size: state.size,
    sortBy: state.sortBy,
    sortDirection: state.sortDirection
  }))

  function onPage(event: DataTablePageEvent): void {
    state.page = event.page
    state.size = event.rows
  }

  function onSort(event: DataTableSortEvent): void {
    state.sortBy = event.sortField || 'id'
    state.sortDirection = (event.sortOrder === -1 ? 'DESC' : 'ASC') as SortDirection
    state.page = 0 // Reset to first page on sort change
  }

  function updateFromResponse(totalElements: number, totalPages: number, first: boolean, last: boolean): void {
    state.totalElements = totalElements
    state.totalPages = totalPages
    state.first = first
    state.last = last
  }

  function reset(): void {
    state.page = 0
    state.size = APP_CONSTANTS.DEFAULT_PAGE_SIZE
    state.sortBy = 'id'
    state.sortDirection = 'ASC'
    state.totalElements = 0
    state.totalPages = 0
  }

  return {
    state,
    params,
    onPage,
    onSort,
    updateFromResponse,
    reset
  }
}

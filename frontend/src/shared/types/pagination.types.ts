/**
 * Standard pagination query parameters sent to backend.
 */
export interface PaginationParams {
  page: number
  size: number
  sortBy: string
  sortDirection: SortDirection
}

export type SortDirection = 'ASC' | 'DESC'

/**
 * Default pagination values.
 */
export const DEFAULT_PAGINATION: PaginationParams = {
  page: 0,
  size: 20,
  sortBy: 'id',
  sortDirection: 'ASC'
}

/**
 * Pagination state for use in stores/composables.
 */
export interface PaginationState {
  page: number
  size: number
  sortBy: string
  sortDirection: SortDirection
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

/**
 * PrimeVue DataTable page event shape.
 */
export interface DataTablePageEvent {
  page: number
  rows: number
  first: number
}

/**
 * PrimeVue DataTable sort event shape.
 */
export interface DataTableSortEvent {
  sortField: string | null
  sortOrder: 1 | -1 | null
}

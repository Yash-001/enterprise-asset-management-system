/**
 * Generic API response wrapper.
 */
export interface ApiResponse<T> {
  data: T
  success: boolean
  message?: string
}

/**
 * Paginated response from Spring Boot.
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

/**
 * Backend error response shape.
 */
export interface ApiErrorResponse {
  status: number
  error: string
  message: string
  path: string
  timestamp: string
}

/**
 * Validation error response.
 */
export interface ValidationErrorResponse {
  errors: Record<string, string[]>
}

/**
 * Pagination query params.
 */
export interface PaginationParams {
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

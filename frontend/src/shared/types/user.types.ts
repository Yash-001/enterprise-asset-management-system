/**
 * Matches backend Role enum.
 */
export type UserRole = 'ADMIN' | 'MANAGER' | 'USER'

/**
 * Authenticated user state (decoded from JWT + stored in Pinia).
 */
export interface AuthUser {
  email: string
  role: UserRole
  firstName: string
  lastName: string
}

/**
 * User response from /api/v1/users/{id} (backend UserResponse DTO).
 */
export interface UserResponse {
  id: number
  firstName: string
  lastName: string
  email: string
  role: UserRole
  active: boolean
  createdAt: string
  updatedAt: string
}

/**
 * User create request (backend UserCreateRequest DTO).
 */
export interface UserCreateRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  role?: UserRole
}

/**
 * User update request (backend UserUpdateRequest DTO).
 */
export interface UserUpdateRequest {
  firstName?: string
  lastName?: string
  role?: UserRole
  active?: boolean
}

/**
 * User search/filter params.
 */
export interface UserSearchParams {
  firstName?: string
  lastName?: string
  email?: string
  role?: UserRole
  active?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

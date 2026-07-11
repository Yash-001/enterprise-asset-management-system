import type { UserRole } from '@/shared/types/user.types'

export interface UserListItem {
  id: number
  firstName: string
  lastName: string
  email: string
  role: UserRole
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface UserCreatePayload {
  firstName: string
  lastName: string
  email: string
  password: string
  role?: UserRole
}

export interface UserUpdatePayload {
  firstName?: string
  lastName?: string
  role?: UserRole
  active?: boolean
}

export interface UserSearchFilters {
  firstName?: string
  lastName?: string
  email?: string
  role?: UserRole | ''
  active?: boolean | ''
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

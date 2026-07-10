import type { UserRole } from '@/shared/types/user.types'

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterPayload {
  firstName: string
  lastName: string
  email: string
  password: string
}

export interface AuthState {
  token: string | null
  user: AuthenticatedUser | null
  isAuthenticated: boolean
}

export interface AuthenticatedUser {
  email: string
  role: UserRole
  firstName: string
  lastName: string
}

export interface DecodedToken {
  sub: string
  role: UserRole
  firstName: string
  lastName: string
  iat: number
  exp: number
}

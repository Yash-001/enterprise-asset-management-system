/**
 * Standard Spring Boot paginated response.
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
  empty: boolean
}

/**
 * Spring Boot error response shape (from GlobalExceptionHandler).
 */
export interface ApiErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}

/**
 * Login response from /api/v1/auth/login.
 */
export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
}

/**
 * Login request payload.
 */
export interface LoginRequest {
  email: string
  password: string
}

/**
 * Register request payload.
 */
export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
}

/**
 * Decoded JWT payload claims.
 */
export interface JwtPayload {
  sub: string
  role: string
  firstName: string
  lastName: string
  iat: number
  exp: number
}

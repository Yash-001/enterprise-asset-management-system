import { apiClient, ENDPOINTS } from '@/api'
import type { LoginResponse } from '@/shared/types'
import type { LoginCredentials, RegisterPayload } from '../types'

/**
 * Auth API service — handles login/register HTTP calls.
 */
export class AuthService {
  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    return apiClient.post<LoginResponse>(ENDPOINTS.AUTH.LOGIN, credentials)
  }

  async register(payload: RegisterPayload): Promise<void> {
    await apiClient.post<void>(ENDPOINTS.AUTH.REGISTER, payload)
  }
}

export const authService = new AuthService()

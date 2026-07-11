import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { UserListItem, UserCreatePayload, UserUpdatePayload, UserSearchFilters } from '../types'

export class UserService {
  async getAll(params: UserSearchFilters = {}): Promise<PageResponse<UserListItem>> {
    return apiClient.getPaged<UserListItem>(ENDPOINTS.USERS.BASE, params as Record<string, unknown>)
  }

  async search(params: UserSearchFilters): Promise<PageResponse<UserListItem>> {
    return apiClient.getPaged<UserListItem>(ENDPOINTS.USERS.SEARCH, params as Record<string, unknown>)
  }

  async getById(id: number): Promise<UserListItem> {
    return apiClient.get<UserListItem>(ENDPOINTS.USERS.BY_ID(id))
  }

  async create(payload: UserCreatePayload): Promise<UserListItem> {
    return apiClient.post<UserListItem>(ENDPOINTS.USERS.BASE, payload)
  }

  async update(id: number, payload: UserUpdatePayload): Promise<UserListItem> {
    return apiClient.put<UserListItem>(ENDPOINTS.USERS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.USERS.BY_ID(id))
  }
}

export const userService = new UserService()

import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { NotificationResponse } from '@/shared/types/response.types'

export class NotificationService {
  async getAll(params: Record<string, unknown> = {}): Promise<PageResponse<NotificationResponse>> {
    return apiClient.getPaged<NotificationResponse>(ENDPOINTS.NOTIFICATIONS.BASE, params)
  }

  async getById(id: number): Promise<NotificationResponse> {
    return apiClient.get<NotificationResponse>(ENDPOINTS.NOTIFICATIONS.BY_ID(id))
  }

  async markAsRead(id: number): Promise<void> {
    return apiClient.put<void>(ENDPOINTS.NOTIFICATIONS.MARK_READ(id))
  }

  async markAllAsRead(recipientUserId?: number): Promise<void> {
    const params = recipientUserId ? { recipientUserId } : {}
    return apiClient.put<void>(ENDPOINTS.NOTIFICATIONS.MARK_ALL_READ, null, { params })
  }

  async getUnreadCount(recipientUserId?: number): Promise<number> {
    const params = recipientUserId ? { recipientUserId } : {}
    return apiClient.get<number>(ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT, { params })
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.NOTIFICATIONS.BY_ID(id))
  }
}

export const notificationService = new NotificationService()

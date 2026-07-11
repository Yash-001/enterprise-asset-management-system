import { apiClient, ENDPOINTS } from '@/api'
import type { NotificationResponse } from '../types'

export class NotificationService {
  async getAll(): Promise<NotificationResponse[]> {
    return apiClient.get<NotificationResponse[]>(ENDPOINTS.NOTIFICATIONS.BASE)
  }

  async getById(id: number): Promise<NotificationResponse> {
    return apiClient.get<NotificationResponse>(ENDPOINTS.NOTIFICATIONS.BY_ID(id))
  }

  async markAsRead(id: number): Promise<void> {
    return apiClient.put<void>(ENDPOINTS.NOTIFICATIONS.MARK_READ(id), {})
  }

  async markAllAsRead(): Promise<void> {
    return apiClient.put<void>(ENDPOINTS.NOTIFICATIONS.MARK_ALL_READ, {})
  }

  async getUnreadCount(): Promise<number> {
    return apiClient.get<number>(ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.NOTIFICATIONS.BY_ID(id))
  }
}

export const notificationService = new NotificationService()

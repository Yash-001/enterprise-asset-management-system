import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useLoadingStore } from '@/shared/stores'
import { notificationService } from '../services'
import type { NotificationResponse } from '../types'

export const useNotificationStore = defineStore('notifications', () => {
  const loadingStore = useLoadingStore()

  const notifications = ref<NotificationResponse[]>([])
  const unreadCount = ref(0)

  async function fetchNotifications(): Promise<void> {
    loadingStore.startLoading()
    try {
      const response = await notificationService.getAll()
      notifications.value = response.content
      unreadCount.value = notifications.value.filter(n => !n.read).length
    } catch {
      notifications.value = []
    } finally {
      loadingStore.stopLoading()
    }
  }

  async function markAsRead(id: number): Promise<void> {
    await notificationService.markAsRead(id)
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.read = true
      unreadCount.value = notifications.value.filter(n => !n.read).length
    }
  }

  async function markAllAsRead(): Promise<void> {
    await notificationService.markAllAsRead()
    notifications.value.forEach(n => (n.read = true))
    unreadCount.value = 0
  }

  function $reset(): void {
    notifications.value = []
    unreadCount.value = 0
  }

  return {
    notifications,
    unreadCount,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    $reset
  }
})

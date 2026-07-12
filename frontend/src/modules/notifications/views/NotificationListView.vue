<template>
  <div>
    <BasePageHeader title="Notifications" subtitle="View and manage your notifications">
      <template #actions>
        <Button
          label="Mark All as Read"
          icon="pi pi-check-circle"
          severity="secondary"
          :disabled="notificationStore.unreadCount === 0"
          @click="handleMarkAllAsRead"
        />
      </template>
    </BasePageHeader>

    <BaseCard>
      <div v-if="notificationStore.notifications.length === 0" class="text-center py-6">
        <i class="pi pi-bell-slash text-4xl text-gray-300 mb-3"></i>
        <p class="text-gray-500">No notifications yet</p>
      </div>

      <div v-else class="flex flex-column gap-2">
        <div
          v-for="notification in notificationStore.notifications"
          :key="notification.id"
          class="notification-card p-3 border-round cursor-pointer"
          :class="{ 'notification-unread': !notification.read }"
          @click="handleRead(notification)"
        >
          <div class="flex justify-content-between align-items-start">
            <div class="flex-1">
              <div class="flex align-items-center gap-2 mb-1">
                <span class="font-semibold">{{ notification.title }}</span>
                <BaseStatusChip :status="notification.priority" />
              </div>
              <p class="text-sm text-gray-600 m-0">{{ notification.message }}</p>
            </div>
            <span class="text-xs text-gray-400 white-space-nowrap ml-3">
              {{ timeAgo(notification.createdAt) }}
            </span>
          </div>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import Button from 'primevue/button'
import { useNotificationStore } from '../store'
import { useAppToast } from '@/shared/composables'
import { timeAgo } from '@/shared/utils'
import type { NotificationResponse } from '../types'

const notificationStore = useNotificationStore()
const { showSuccess, showApiError } = useAppToast()

onMounted(() => loadData())

async function loadData(): Promise<void> {
  await notificationStore.fetchNotifications()
}

async function handleRead(notification: NotificationResponse): Promise<void> {
  if (!notification.read) {
    try {
      await notificationStore.markAsRead(notification.id)
    } catch (err) {
      showApiError(err)
    }
  }
}

async function handleMarkAllAsRead(): Promise<void> {
  try {
    await notificationStore.markAllAsRead()
    showSuccess('Done', 'All notifications marked as read')
  } catch (err) {
    showApiError(err)
  }
}
</script>

<style scoped>
.notification-card {
  border: 1px solid var(--surface-border);
  transition: background-color 0.2s;
}

.notification-card:hover {
  background-color: var(--surface-hover);
}

.notification-unread {
  border-left: 4px solid var(--primary-color);
  background-color: var(--primary-50, #f0f9ff);
}
</style>

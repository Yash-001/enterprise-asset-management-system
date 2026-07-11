<template>
  <BaseCard title="Recent Activity">
    <div v-if="activities.length === 0" class="no-activity">
      <i class="pi pi-clock"></i>
      <p>No recent activity</p>
    </div>
    <div v-else class="activity-feed">
      <div v-for="item in activities" :key="item.id" class="activity-item">
        <div class="activity-icon" :style="{ backgroundColor: item.color + '20', color: item.color }">
          <i :class="item.icon"></i>
        </div>
        <div class="activity-content">
          <span class="activity-title">{{ item.title }}</span>
          <span class="activity-desc">{{ item.description }}</span>
        </div>
        <span class="activity-time">{{ formatTime(item.timestamp) }}</span>
      </div>
    </div>
  </BaseCard>
</template>

<script setup lang="ts">
import { timeAgo } from '@/shared/utils'
import type { RecentActivity } from '../types'

defineProps<{
  activities: RecentActivity[]
}>()

function formatTime(timestamp: string): string {
  return timeAgo(timestamp)
}
</script>

<style scoped>
.activity-feed {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--eams-border);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.activity-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--eams-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-desc {
  font-size: 0.75rem;
  color: var(--eams-text-muted);
}

.activity-time {
  font-size: 0.7rem;
  color: var(--eams-text-subtle);
  white-space: nowrap;
}

.no-activity {
  text-align: center;
  padding: 2rem;
  color: var(--eams-text-muted);
}

.no-activity i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  display: block;
  opacity: 0.5;
}
</style>

<template>
  <div class="stats-grid">
    <BaseStatsCard
      label="Total Assets"
      :value="stats.totalAssets"
      icon="pi pi-box"
      color="#3b82f6"
    />
    <BaseStatsCard
      label="Open Work Orders"
      :value="stats.openWorkOrders"
      icon="pi pi-wrench"
      color="#f59e0b"
    />
    <BaseStatsCard
      label="Overdue Maintenance"
      :value="stats.overdueMaintenance"
      icon="pi pi-exclamation-triangle"
      color="#ef4444"
    />
    <BaseStatsCard
      label="Low Stock Items"
      :value="stats.lowStockItems"
      icon="pi pi-warehouse"
      color="#8b5cf6"
    />
  </div>
</template>

<script setup lang="ts">
import type { DashboardStats } from '../types'

defineProps<{
  stats: DashboardStats
}>()
</script>

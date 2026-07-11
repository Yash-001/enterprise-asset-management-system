<template>
  <div class="dashboard">
    <BasePageHeader title="Dashboard" subtitle="Overview of your asset management system" />

    <!-- Stats -->
    <StatsOverview :stats="dashboardStore.stats" />

    <!-- Content Grid -->
    <div class="dashboard-grid">
      <!-- Quick Actions -->
      <QuickActions />

      <!-- Recent Activity -->
      <RecentActivityFeed :activities="recentActivities" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useDashboardStore } from '../store'
import StatsOverview from '../components/StatsOverview.vue'
import QuickActions from '../components/QuickActions.vue'
import RecentActivityFeed from '../components/RecentActivityFeed.vue'
import type { RecentActivity } from '../types'

const dashboardStore = useDashboardStore()

// Placeholder — will be fetched from notifications/activity endpoint
const recentActivities = ref<RecentActivity[]>([])

onMounted(async () => {
  await dashboardStore.fetchStats()
})
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>

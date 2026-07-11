<template>
  <MaintenanceBanner />
  <GlobalLoadingBar />
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useThemeStore } from '@/shared/stores'
import { eventBus, EVENT_NAMES } from '@/shared/services'
import { useRouter } from 'vue-router'
import { ROUTE_NAMES } from '@/shared/constants'
import GlobalLoadingBar from '@/shared/components/GlobalLoadingBar.vue'
import ErrorBoundary from '@/shared/components/ErrorBoundary.vue'
import MaintenanceBanner from '@/shared/components/MaintenanceBanner.vue'

const themeStore = useThemeStore()
const router = useRouter()

function handleSessionExpired(): void {
  router.push({ name: ROUTE_NAMES.LOGIN })
}

onMounted(() => {
  themeStore.initTheme()
  eventBus.on(EVENT_NAMES.SESSION_EXPIRED, handleSessionExpired)
})

onUnmounted(() => {
  eventBus.off(EVENT_NAMES.SESSION_EXPIRED, handleSessionExpired)
})
</script>

<template>
  <MaintenanceBanner />
  <GlobalLoadingBar />
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useThemeStore } from '@/shared/stores'
import { eventBus, EVENT_NAMES } from '@/shared/services'
import { useRouter } from 'vue-router'
import { ROUTE_NAMES } from '@/shared/constants'
import GlobalLoadingBar from '@/shared/components/GlobalLoadingBar.vue'
import ErrorBoundary from '@/shared/components/ErrorBoundary.vue'
import MaintenanceBanner from '@/shared/components/MaintenanceBanner.vue'

const themeStore = useThemeStore()
const router = useRouter()

onMounted(() => {
  themeStore.initTheme()

  // Listen for session expiry events from interceptors
  eventBus.on(EVENT_NAMES.SESSION_EXPIRED, () => {
    router.push({ name: ROUTE_NAMES.LOGIN })
  })
})
</script>

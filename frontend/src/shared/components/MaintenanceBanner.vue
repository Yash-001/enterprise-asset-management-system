<template>
  <Transition name="banner">
    <div v-if="showBanner" class="maintenance-banner">
      <i class="pi pi-exclamation-triangle"></i>
      <span>System is currently under maintenance. Some features may be unavailable.</span>
      <button class="banner-close" @click="dismiss">
        <i class="pi pi-times"></i>
      </button>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { apiClient, ENDPOINTS } from '@/api'

const showBanner = ref(false)

onMounted(async () => {
  try {
    const health = await apiClient.get<{ status: string }>(ENDPOINTS.HEALTH)
    if (health.status !== 'UP') {
      showBanner.value = true
    }
  } catch {
    // Don't show maintenance banner for network errors during dev
    // Only show if backend explicitly reports non-UP status
    showBanner.value = false
  }
})

function dismiss(): void {
  showBanner.value = false
}
</script>

<style scoped>
.maintenance-banner {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.6rem 1.25rem;
  background: var(--eams-warning);
  color: #1a1a1a;
  font-size: 0.8rem;
  font-weight: 500;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10000;
}

.banner-close {
  margin-left: auto;
  background: none;
  border: none;
  cursor: pointer;
  color: #1a1a1a;
  padding: 0.25rem;
  border-radius: 4px;
}

.banner-close:hover {
  background: rgba(0, 0, 0, 0.1);
}

.banner-enter-active,
.banner-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.banner-enter-from,
.banner-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}
</style>

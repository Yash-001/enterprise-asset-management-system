<template>
  <Transition name="loading-bar">
    <div v-if="loadingStore.isLoading" class="global-loading-bar">
      <div class="global-loading-bar__progress"></div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { useLoadingStore } from '@/shared/stores'

const loadingStore = useLoadingStore()
</script>

<style scoped>
.global-loading-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 9999;
  overflow: hidden;
  background: var(--eams-primary-light);
}

.global-loading-bar__progress {
  height: 100%;
  background: var(--eams-primary);
  animation: loading-slide 1.5s infinite ease-in-out;
  border-radius: 0 2px 2px 0;
}

@keyframes loading-slide {
  0% {
    width: 0;
    margin-left: 0;
  }
  50% {
    width: 60%;
    margin-left: 20%;
  }
  100% {
    width: 0;
    margin-left: 100%;
  }
}

.loading-bar-enter-active,
.loading-bar-leave-active {
  transition: opacity 0.3s ease;
}

.loading-bar-enter-from,
.loading-bar-leave-to {
  opacity: 0;
}
</style>

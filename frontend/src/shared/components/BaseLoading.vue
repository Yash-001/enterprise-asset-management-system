<template>
  <div class="base-loading" :class="{ 'base-loading--overlay': overlay }">
    <i class="pi pi-spin pi-spinner base-loading__spinner"></i>
    <span v-if="message" class="base-loading__message">{{ message }}</span>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    message?: string
    overlay?: boolean
  }>(),
  {
    message: 'Loading...',
    overlay: false
  }
)
</script>

<style scoped>
.base-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 2rem;
}

.base-loading--overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  z-index: 10;
  border-radius: var(--eams-radius-md);
}

:global(.app-dark) .base-loading--overlay {
  background: rgba(15, 23, 42, 0.8);
}

.base-loading__spinner {
  font-size: 2rem;
  color: var(--eams-primary);
}

.base-loading__message {
  font-size: 0.85rem;
  color: var(--eams-text-muted);
}
</style>

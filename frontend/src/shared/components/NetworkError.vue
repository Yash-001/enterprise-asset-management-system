<template>
  <div class="network-error">
    <div class="network-error__content">
      <i class="pi pi-wifi network-error__icon"></i>
      <h3>Connection Error</h3>
      <p>{{ message }}</p>
      <Button label="Retry" icon="pi pi-refresh" @click="$emit('retry')" :loading="retrying" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'

withDefaults(
  defineProps<{
    message?: string
    retrying?: boolean
  }>(),
  {
    message: 'Unable to connect to the server. Please check your internet connection and try again.',
    retrying: false
  }
)

defineEmits<{ retry: [] }>()
</script>

<style scoped>
.network-error {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 1.5rem;
}

.network-error__content {
  text-align: center;
  max-width: 400px;
}

.network-error__icon {
  font-size: 3rem;
  color: var(--eams-warning);
  margin-bottom: 1rem;
}

h3 {
  font-size: 1.25rem;
  color: var(--eams-text);
  margin: 0 0 0.5rem;
}

p {
  color: var(--eams-text-muted);
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  line-height: 1.5;
}
</style>

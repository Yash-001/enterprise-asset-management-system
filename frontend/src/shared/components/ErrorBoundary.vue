<template>
  <div v-if="hasError" class="error-boundary">
    <div class="error-boundary__content">
      <i class="pi pi-exclamation-circle error-boundary__icon"></i>
      <h2>Something went wrong</h2>
      <p>An unexpected error occurred. Please try again.</p>
      <div class="error-boundary__actions">
        <Button label="Reload Page" icon="pi pi-refresh" @click="handleReload" />
        <Button label="Go Home" icon="pi pi-home" severity="secondary" outlined @click="handleGoHome" />
      </div>
      <details v-if="isDev" class="error-boundary__details">
        <summary>Error Details</summary>
        <pre>{{ errorMessage }}</pre>
      </details>
    </div>
  </div>
  <slot v-else />
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import { logger } from '@/shared/utils'

const router = useRouter()
const hasError = ref(false)
const errorMessage = ref('')
const isDev = import.meta.env.DEV

onErrorCaptured((err: Error) => {
  hasError.value = true
  errorMessage.value = `${err.name}: ${err.message}\n${err.stack || ''}`
  logger.error('ErrorBoundary caught:', err.message, err.stack)
  return false // prevent propagation
})

function handleReload(): void {
  window.location.reload()
}

function handleGoHome(): void {
  hasError.value = false
  router.push('/')
}
</script>

<style scoped>
.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 2rem;
}

.error-boundary__content {
  text-align: center;
  max-width: 500px;
}

.error-boundary__icon {
  font-size: 4rem;
  color: var(--eams-danger);
  margin-bottom: 1rem;
}

h2 {
  font-size: 1.5rem;
  color: var(--eams-text);
  margin: 0 0 0.5rem;
}

p {
  color: var(--eams-text-muted);
  margin-bottom: 1.5rem;
}

.error-boundary__actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
}

.error-boundary__details {
  margin-top: 1.5rem;
  text-align: left;
}

.error-boundary__details summary {
  cursor: pointer;
  color: var(--eams-text-muted);
  font-size: 0.8rem;
}

.error-boundary__details pre {
  margin-top: 0.5rem;
  padding: 0.75rem;
  background: var(--eams-hover);
  border-radius: var(--eams-radius-sm);
  font-size: 0.7rem;
  overflow-x: auto;
  max-height: 200px;
  color: var(--eams-danger);
}
</style>

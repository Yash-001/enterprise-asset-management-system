<template>
  <div class="error-page">
    <div class="error-content">
      <i class="pi pi-lock error-icon"></i>
      <h1>403</h1>
      <h2>Access Denied</h2>
      <p>You do not have permission to access this page.</p>
      <Button label="Go to Dashboard" icon="pi pi-home" @click="router.push('/')" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Button from 'primevue/button'

const router = useRouter()
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--p-surface-ground);
}

.error-content {
  text-align: center;
}

.error-icon {
  font-size: 4rem;
  color: var(--p-red-500);
  margin-bottom: 1rem;
}

h1 {
  font-size: 5rem;
  font-weight: 800;
  color: var(--p-text-color);
  margin: 0;
}

h2 {
  font-size: 1.5rem;
  color: var(--p-text-muted-color);
  margin: 0.5rem 0;
}

p {
  color: var(--p-text-muted-color);
  margin-bottom: 2rem;
}
</style>

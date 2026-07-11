<template>
  <div class="page-header">
    <div class="page-header__left">
      <h1 class="page-header__title">{{ title }}</h1>
      <p v-if="subtitle" class="page-header__subtitle">{{ subtitle }}</p>
    </div>
    <div class="page-header__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-header__title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--eams-text);
  margin: 0;
}

.page-header__subtitle {
  font-size: 0.875rem;
  color: var(--eams-text-muted);
  margin-top: 0.25rem;
}

.page-header__actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}
</style>

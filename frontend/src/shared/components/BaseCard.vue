<template>
  <div class="base-card" :class="{ 'base-card--flat': flat }">
    <div v-if="title || $slots.header" class="base-card__header">
      <slot name="header">
        <h3 class="base-card__title">{{ title }}</h3>
      </slot>
      <div class="base-card__header-actions">
        <slot name="header-actions" />
      </div>
    </div>
    <div class="base-card__body">
      <slot />
    </div>
    <div v-if="$slots.footer" class="base-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string
  flat?: boolean
}>()
</script>

<style scoped>
.base-card {
  background: var(--eams-card);
  border: 1px solid var(--eams-border);
  border-radius: var(--eams-radius-md);
  box-shadow: var(--eams-shadow-sm);
  overflow: hidden;
}

.base-card--flat {
  box-shadow: none;
}

.base-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--eams-border);
}

.base-card__title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--eams-text);
  margin: 0;
}

.base-card__header-actions {
  display: flex;
  gap: 0.5rem;
}

.base-card__body {
  padding: 1.25rem;
}

.base-card__footer {
  padding: 0.75rem 1.25rem;
  border-top: 1px solid var(--eams-border);
  background: var(--eams-hover);
}
</style>

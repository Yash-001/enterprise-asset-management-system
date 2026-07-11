<template>
  <div class="empty-state">
    <i :class="['empty-state__icon', icon]"></i>
    <h3 class="empty-state__title">{{ title }}</h3>
    <p v-if="description" class="empty-state__description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-state__action">
      <slot name="action" />
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    icon?: string
    title: string
    description?: string
  }>(),
  {
    icon: 'pi pi-inbox'
  }
)
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 3rem 1.5rem;
}

.empty-state__icon {
  font-size: 3rem;
  color: var(--eams-text-subtle);
  margin-bottom: 1rem;
  display: block;
}

.empty-state__title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--eams-text);
  margin: 0 0 0.5rem;
}

.empty-state__description {
  font-size: 0.875rem;
  color: var(--eams-text-muted);
  max-width: 400px;
  margin: 0 auto 1.5rem;
}

.empty-state__action {
  margin-top: 1rem;
}
</style>

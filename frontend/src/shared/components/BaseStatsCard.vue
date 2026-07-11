<template>
  <div class="stats-card" :style="{ borderLeftColor: color }">
    <div class="stats-card__content">
      <span class="stats-card__label">{{ label }}</span>
      <span class="stats-card__value">{{ prefix }}{{ formattedValue }}</span>
      <span v-if="trend !== undefined" class="stats-card__trend" :class="trendClass">
        <i :class="['pi', trend >= 0 ? 'pi-arrow-up' : 'pi-arrow-down']"></i>
        {{ Math.abs(trend) }}%
      </span>
    </div>
    <div class="stats-card__icon">
      <i :class="icon" :style="{ color }"></i>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  label: string
  value: number | string
  icon?: string
  color?: string
  trend?: number
  prefix?: string
}>()

const formattedValue = computed(() => {
  if (typeof props.value === 'number') {
    return props.value.toLocaleString()
  }
  return props.value
})

const trendClass = computed(() => ({
  'stats-card__trend--positive': (props.trend ?? 0) >= 0,
  'stats-card__trend--negative': (props.trend ?? 0) < 0
}))
</script>

<style scoped>
.stats-card {
  background: var(--eams-card);
  border-radius: var(--eams-radius-md);
  padding: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-left: 4px solid var(--eams-primary);
  box-shadow: var(--eams-shadow-sm);
  transition: transform var(--eams-transition), box-shadow var(--eams-transition);
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--eams-shadow-md);
}

.stats-card__content {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.stats-card__label {
  font-size: 0.75rem;
  color: var(--eams-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.stats-card__value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--eams-text);
  line-height: 1.2;
}

.stats-card__trend {
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.2rem;
}

.stats-card__trend--positive {
  color: var(--eams-success);
}

.stats-card__trend--negative {
  color: var(--eams-danger);
}

.stats-card__icon {
  font-size: 2rem;
  opacity: 0.7;
}
</style>

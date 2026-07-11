<template>
  <span class="status-chip" :class="`status-chip--${severity}`">
    {{ label }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  status: string
  statusMap?: Record<string, { label?: string; severity: string }>
}>()

const defaultMap: Record<string, { label?: string; severity: string }> = {
  AVAILABLE: { severity: 'success' },
  ASSIGNED: { severity: 'info' },
  IN_MAINTENANCE: { severity: 'warning' },
  DISPOSED: { severity: 'danger' },
  REQUESTED: { severity: 'info' },
  IN_PROGRESS: { severity: 'warning' },
  COMPLETED: { severity: 'success' },
  CANCELLED: { severity: 'danger' },
  OVERDUE: { severity: 'danger' },
  SCHEDULED: { severity: 'info' },
  DRAFT: { severity: 'neutral' },
  APPROVED: { severity: 'info' },
  ORDERED: { severity: 'warning' },
  RECEIVED: { severity: 'success' },
  ACTIVE: { severity: 'success' },
  INACTIVE: { severity: 'neutral' },
  RETURNED: { severity: 'neutral' },
  LOW: { severity: 'neutral' },
  MEDIUM: { severity: 'info' },
  HIGH: { severity: 'warning' },
  CRITICAL: { severity: 'danger' }
}

const map = computed(() => props.statusMap || defaultMap)
const entry = computed(() => map.value[props.status] || { severity: 'neutral' })
const label = computed(() => entry.value.label || (props.status ? props.status.replace(/_/g, ' ') : '—'))
const severity = computed(() => entry.value.severity)
</script>

<style scoped>
.status-chip {
  display: inline-flex;
  align-items: center;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

.status-chip--success {
  background: color-mix(in srgb, var(--eams-success) 15%, transparent);
  color: var(--eams-success);
}

.status-chip--warning {
  background: color-mix(in srgb, var(--eams-warning) 15%, transparent);
  color: var(--eams-warning);
}

.status-chip--danger {
  background: color-mix(in srgb, var(--eams-danger) 15%, transparent);
  color: var(--eams-danger);
}

.status-chip--info {
  background: color-mix(in srgb, var(--eams-info) 15%, transparent);
  color: var(--eams-info);
}

.status-chip--neutral {
  background: var(--eams-hover);
  color: var(--eams-text-muted);
}
</style>

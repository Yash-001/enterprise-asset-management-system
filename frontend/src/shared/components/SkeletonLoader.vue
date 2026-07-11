<template>
  <div class="skeleton-loader">
    <div v-for="n in rows" :key="n" class="skeleton-row">
      <div
        v-for="col in columns"
        :key="col"
        class="skeleton-cell"
        :style="{ width: getWidth(col) }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    rows?: number
    columns?: number
    variant?: 'table' | 'card' | 'list'
  }>(),
  {
    rows: 5,
    columns: 4,
    variant: 'table'
  }
)

function getWidth(index: number): string {
  const widths = ['20%', '35%', '25%', '20%', '15%', '10%']
  return widths[index % widths.length]
}
</script>

<style scoped>
.skeleton-loader {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1rem 0;
}

.skeleton-row {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.skeleton-cell {
  height: 16px;
  background: linear-gradient(
    90deg,
    var(--eams-hover) 25%,
    var(--eams-border) 50%,
    var(--eams-hover) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>

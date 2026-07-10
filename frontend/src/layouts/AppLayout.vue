<template>
  <div class="layout-wrapper">
    <main class="layout-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
// AppLayout — sidebar and topbar will be added in Chunk 2
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  min-height: 100vh;
}

.layout-content {
  flex: 1;
  padding: 1.5rem;
  background: var(--p-surface-ground);
}
</style>

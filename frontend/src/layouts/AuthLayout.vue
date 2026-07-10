<template>
  <div class="auth-layout">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// AuthLayout — minimal wrapper for login/register pages (no sidebar/topbar)
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--p-surface-ground);
}
</style>

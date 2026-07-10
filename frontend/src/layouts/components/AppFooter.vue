<template>
  <footer class="app-footer">
    <span class="footer-text">© {{ year }} EAMS — Enterprise Asset Management System</span>
    <span class="footer-version">v1.0.0</span>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1.5rem;
  border-top: 1px solid var(--p-surface-border);
  background: var(--p-surface-card);
  font-size: 0.75rem;
  color: var(--p-text-muted-color);
}

.footer-version {
  font-weight: 600;
}

@media (max-width: 768px) {
  .app-footer {
    flex-direction: column;
    gap: 0.25rem;
    text-align: center;
  }
}
</style>

<template>
  <div class="layout-wrapper">
    <AppSidebar />

    <div class="layout-main" :class="{ 'sidebar-collapsed': uiStore.isSidebarCollapsed }">
      <AppTopbar />

      <main class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>

      <AppFooter />
    </div>
  </div>

  <!-- Global Toast -->
  <Toast position="top-right" />
  <ConfirmDialog />
</template>

<script setup lang="ts">
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'
import AppSidebar from './components/AppSidebar.vue'
import AppTopbar from './components/AppTopbar.vue'
import AppFooter from './components/AppFooter.vue'
import { useUIStore } from '@/shared/stores'

const uiStore = useUIStore()
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  min-height: 100vh;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  margin-left: 260px;
  transition: margin-left 0.3s ease;
}

.layout-main.sidebar-collapsed {
  margin-left: 64px;
}

.layout-content {
  flex: 1;
  padding: 1.5rem;
  background: var(--p-surface-ground);
}

/* Route transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .layout-main {
    margin-left: 0;
  }

  .layout-main.sidebar-collapsed {
    margin-left: 0;
  }

  .layout-content {
    padding: 1rem;
  }
}
</style>

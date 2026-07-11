<template>
  <div class="layout-wrapper">
    <!-- Mobile overlay -->
    <div
      v-if="isMobileMenuOpen"
      class="layout-overlay"
      @click="closeMobileMenu"
    ></div>

    <AppSidebar :class="{ 'mobile-open': isMobileMenuOpen }" />

    <div class="layout-main" :class="{ 'sidebar-collapsed': uiStore.isSidebarCollapsed }">
      <AppTopbar @toggle-mobile-menu="toggleMobileMenu" />

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

  <Toast position="top-right" />
  <ConfirmDialog />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Toast from 'primevue/toast'
import ConfirmDialog from 'primevue/confirmdialog'
import AppSidebar from './components/AppSidebar.vue'
import AppTopbar from './components/AppTopbar.vue'
import AppFooter from './components/AppFooter.vue'
import { useUIStore } from '@/shared/stores'

const uiStore = useUIStore()
const router = useRouter()
const isMobileMenuOpen = ref(false)

function toggleMobileMenu(): void {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function closeMobileMenu(): void {
  isMobileMenuOpen.value = false
}

// Close mobile menu on route change
router.afterEach(() => {
  isMobileMenuOpen.value = false
})
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  min-height: 100vh;
}

.layout-overlay {
  display: none;
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
  background: var(--eams-bg);
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

/* ─── Mobile ─────────────────────────────────────────────── */
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

  .layout-overlay {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 999;
    animation: fadeIn 0.2s ease;
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>

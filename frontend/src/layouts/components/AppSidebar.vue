<template>
  <aside class="sidebar" :class="{ collapsed: uiStore.isSidebarCollapsed }">
    <!-- Logo -->
    <div class="sidebar-header">
      <span v-if="!uiStore.isSidebarCollapsed" class="logo-text">EAMS</span>
      <span v-else class="logo-icon">E</span>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav" aria-label="Main navigation">
      <div v-for="section in visibleSections" :key="section.title" class="nav-section">
        <span v-if="!uiStore.isSidebarCollapsed" class="nav-section-title">
          {{ section.title }}
        </span>
        <ul>
          <li v-for="item in section.items" :key="item.routeName">
            <router-link
              :to="{ name: item.routeName }"
              class="nav-item"
              active-class="nav-item--active"
            >
              <i :class="item.icon"></i>
              <span v-if="!uiStore.isSidebarCollapsed" class="nav-label">{{ item.label }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </nav>

    <!-- Collapse toggle -->
    <div class="sidebar-footer">
      <button class="collapse-btn" @click="uiStore.toggleSidebar" :aria-label="uiStore.isSidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'">
        <i :class="['pi', uiStore.isSidebarCollapsed ? 'pi-angle-right' : 'pi-angle-left']"></i>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUIStore } from '@/shared/stores'
import { useAuthStore } from '@/modules/auth'
import { ROLE_PERMISSIONS } from '@/shared/constants/permission.constants'
import { isFeatureEnabled, type FeatureFlag } from '@/shared/config'
import { navigationConfig, type NavSection } from '../config/navigation.config'

const uiStore = useUIStore()
const authStore = useAuthStore()

/**
 * Filter navigation sections and items based on user permissions and feature flags.
 */
const visibleSections = computed<NavSection[]>(() => {
  const userRole = authStore.userRole
  const userPermissions = userRole ? ROLE_PERMISSIONS[userRole] || [] : []

  return navigationConfig
    .map((section) => ({
      ...section,
      items: section.items.filter((item) => {
        // Feature flag check
        if (item.featureFlag && !isFeatureEnabled(item.featureFlag as FeatureFlag)) {
          return false
        }
        // Permission check — empty permissions means visible to all authenticated users
        if (item.permissions.length === 0) return true
        return item.permissions.some((p) => userPermissions.includes(p))
      })
    }))
    .filter((section) => section.items.length > 0)
})
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 260px;
  height: 100vh;
  background: var(--p-surface-card);
  border-right: 1px solid var(--p-surface-border);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  z-index: 1000;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 1.25rem;
  text-align: center;
  border-bottom: 1px solid var(--p-surface-border);
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--p-primary-color);
  letter-spacing: -0.5px;
}

.logo-icon {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--p-primary-color);
}

.sidebar-nav {
  flex: 1;
  padding: 0.75rem 0.5rem;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 0.75rem;
}

.nav-section-title {
  display: block;
  padding: 0.25rem 0.75rem;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--p-text-muted-color);
  margin-bottom: 0.25rem;
}

.nav-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  color: var(--p-text-color);
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background 0.2s, color 0.2s;
  margin-bottom: 2px;
}

.nav-item:hover {
  background: var(--p-surface-hover);
}

.nav-item--active {
  background: var(--p-primary-color);
  color: var(--p-primary-contrast-color);
}

.nav-item--active:hover {
  background: var(--p-primary-color);
}

.nav-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-footer {
  padding: 0.75rem;
  border-top: 1px solid var(--p-surface-border);
  text-align: center;
}

.collapse-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  color: var(--p-text-muted-color);
  transition: background 0.2s;
}

.collapse-btn:hover {
  background: var(--p-surface-hover);
}

/* Collapsed state adjustments */
.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 0.6rem;
}

.sidebar.collapsed .nav-section-title {
  display: none;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    position: fixed;
    transition: transform 0.3s ease;
  }

  .sidebar.mobile-open {
    transform: translateX(0);
    box-shadow: var(--eams-shadow-lg);
  }
}
</style>

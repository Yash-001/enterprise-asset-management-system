<template>
  <header class="topbar">
    <div class="topbar-left">
      <button class="topbar-btn hamburger" @click="uiStore.toggleSidebar">
        <i class="pi pi-bars"></i>
      </button>
      <Breadcrumb :model="breadcrumbItems" class="topbar-breadcrumb" />
    </div>

    <div class="topbar-right">
      <!-- Dark mode toggle -->
      <button class="topbar-btn" @click="themeStore.toggleDark" v-tooltip.bottom="'Toggle Dark Mode'">
        <i :class="['pi', themeStore.isDark ? 'pi-sun' : 'pi-moon']"></i>
      </button>

      <!-- Notifications bell -->
      <button class="topbar-btn notification-btn" @click="router.push({ name: ROUTE_NAMES.NOTIFICATIONS })" v-tooltip.bottom="'Notifications'">
        <i class="pi pi-bell"></i>
        <span v-if="unreadCount > 0" class="badge">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </span>
      </button>

      <!-- User info -->
      <div class="user-info">
        <div class="user-avatar">
          {{ initials }}
        </div>
        <div class="user-details">
          <span class="user-name">{{ authStore.fullName }}</span>
          <span class="user-role">{{ authStore.userRole }}</span>
        </div>
      </div>

      <!-- Logout -->
      <button class="topbar-btn" @click="handleLogout" v-tooltip.bottom="'Logout'">
        <i class="pi pi-sign-out"></i>
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Breadcrumb from 'primevue/breadcrumb'
import { useAuthStore } from '@/modules/auth'
import { useUIStore, useThemeStore } from '@/shared/stores'
import { ROUTE_NAMES } from '@/shared/constants'
import { getInitials } from '@/shared/utils'

const router = useRouter()
const authStore = useAuthStore()
const uiStore = useUIStore()
const themeStore = useThemeStore()

const unreadCount = ref(0)

const initials = computed(() =>
  getInitials(authStore.user?.firstName || '', authStore.user?.lastName || '')
)

const breadcrumbItems = computed(() =>
  uiStore.breadcrumbs.map((b) => ({
    label: b.label,
    to: b.to
  }))
)

function handleLogout(): void {
  authStore.logout()
  router.push({ name: ROUTE_NAMES.LOGIN })
}

onMounted(() => {
  themeStore.initTheme()
})
</script>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  height: 60px;
  background: var(--p-surface-card);
  border-bottom: 1px solid var(--p-surface-border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.topbar-breadcrumb {
  background: transparent;
  border: none;
  padding: 0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.topbar-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  color: var(--p-text-muted-color);
  font-size: 1.1rem;
  transition: background 0.2s, color 0.2s;
  position: relative;
}

.topbar-btn:hover {
  background: var(--p-surface-hover);
  color: var(--p-text-color);
}

.notification-btn {
  position: relative;
}

.badge {
  position: absolute;
  top: 2px;
  right: 2px;
  background: var(--p-red-500);
  color: white;
  font-size: 0.6rem;
  padding: 0.1rem 0.3rem;
  border-radius: 10px;
  font-weight: 700;
  min-width: 16px;
  text-align: center;
  line-height: 1.2;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 0.5rem;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--p-primary-color);
  color: var(--p-primary-contrast-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--p-text-color);
  line-height: 1.2;
}

.user-role {
  font-size: 0.65rem;
  color: var(--p-text-muted-color);
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.hamburger {
  display: none;
}

@media (max-width: 768px) {
  .hamburger {
    display: flex;
  }

  .user-details {
    display: none;
  }

  .topbar-breadcrumb {
    display: none;
  }
}
</style>

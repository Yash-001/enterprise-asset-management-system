import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { STORAGE_KEYS } from '@/shared/constants'

/**
 * Global UI state store.
 * Manages sidebar, breadcrumbs, and UI preferences.
 */
export const useUIStore = defineStore('ui', () => {
  // ─── State ────────────────────────────────────────────────────────────────
  const sidebarCollapsed = ref(
    localStorage.getItem(STORAGE_KEYS.SIDEBAR_COLLAPSED) === 'true'
  )
  const breadcrumbs = ref<Array<{ label: string; to?: string }>>([])
  const pageTitle = ref('')

  // ─── Getters ──────────────────────────────────────────────────────────────
  const isSidebarCollapsed = computed(() => sidebarCollapsed.value)

  // ─── Actions ──────────────────────────────────────────────────────────────
  function toggleSidebar(): void {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem(STORAGE_KEYS.SIDEBAR_COLLAPSED, String(sidebarCollapsed.value))
  }

  function collapseSidebar(): void {
    sidebarCollapsed.value = true
    localStorage.setItem(STORAGE_KEYS.SIDEBAR_COLLAPSED, 'true')
  }

  function expandSidebar(): void {
    sidebarCollapsed.value = false
    localStorage.setItem(STORAGE_KEYS.SIDEBAR_COLLAPSED, 'false')
  }

  function setBreadcrumbs(items: Array<{ label: string; to?: string }>): void {
    breadcrumbs.value = items
  }

  function setPageTitle(title: string): void {
    pageTitle.value = title
    document.title = title ? `${title} | EAMS` : 'EAMS'
  }

  function $reset(): void {
    sidebarCollapsed.value = false
    breadcrumbs.value = []
    pageTitle.value = ''
  }

  return {
    sidebarCollapsed,
    breadcrumbs,
    pageTitle,
    isSidebarCollapsed,
    toggleSidebar,
    collapseSidebar,
    expandSidebar,
    setBreadcrumbs,
    setPageTitle,
    $reset
  }
})

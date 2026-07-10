import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { STORAGE_KEYS } from '@/shared/constants'
import { eventBus, EVENT_NAMES } from '@/shared/services'

/**
 * Theme store — manages dark/light mode.
 * Persists preference to localStorage. Respects OS preference as default.
 */
export const useThemeStore = defineStore('theme', () => {
  // ─── State ────────────────────────────────────────────────────────────────
  const isDark = ref(false)

  // ─── Getters ──────────────────────────────────────────────────────────────
  const currentTheme = computed(() => (isDark.value ? 'dark' : 'light'))

  // ─── Actions ──────────────────────────────────────────────────────────────

  /**
   * Initialize theme on app startup.
   * Priority: localStorage > OS preference > light.
   */
  function initTheme(): void {
    const saved = localStorage.getItem(STORAGE_KEYS.THEME)
    if (saved === 'dark') {
      enableDark()
    } else if (saved === 'light') {
      disableDark()
    } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
      enableDark()
    } else {
      disableDark()
    }
  }

  function toggleDark(): void {
    isDark.value ? disableDark() : enableDark()
  }

  function enableDark(): void {
    isDark.value = true
    document.documentElement.classList.add('app-dark')
    localStorage.setItem(STORAGE_KEYS.THEME, 'dark')
    eventBus.emit(EVENT_NAMES.THEME_CHANGED, { isDark: true })
  }

  function disableDark(): void {
    isDark.value = false
    document.documentElement.classList.remove('app-dark')
    localStorage.setItem(STORAGE_KEYS.THEME, 'light')
    eventBus.emit(EVENT_NAMES.THEME_CHANGED, { isDark: false })
  }

  function $reset(): void {
    disableDark()
  }

  return {
    isDark,
    currentTheme,
    initTheme,
    toggleDark,
    enableDark,
    disableDark,
    $reset
  }
})

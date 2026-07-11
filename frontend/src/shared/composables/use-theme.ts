import { computed } from 'vue'
import { useThemeStore } from '@/shared/stores'

/**
 * Composable for theme access in components.
 *
 * Usage:
 *   const { isDark, toggleDark, currentTheme } = useTheme()
 */
export function useTheme() {
  const themeStore = useThemeStore()

  const isDark = computed(() => themeStore.isDark)
  const currentTheme = computed(() => themeStore.currentTheme)

  function toggleDark(): void {
    themeStore.toggleDark()
  }

  function initTheme(): void {
    themeStore.initTheme()
  }

  return {
    isDark,
    currentTheme,
    toggleDark,
    initTheme
  }
}

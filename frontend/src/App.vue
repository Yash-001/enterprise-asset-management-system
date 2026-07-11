<template>
  <router-view />
</template>

<script setup lang="ts">
import { useThemeStore } from '@/shared/stores'

// Initialize theme (dark/light) on app mount
const themeStore = useThemeStore()
themeStore.initTheme()
</script>

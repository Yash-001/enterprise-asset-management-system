import { computed, type ComputedRef } from 'vue'
import { FEATURE_FLAGS, type FeatureFlag } from '@/shared/config'

/**
 * Composable for checking feature flags in components.
 *
 * Usage:
 *   const { isEnabled } = useFeatureFlag('NOTIFICATIONS')
 *   <div v-if="isEnabled">...</div>
 */
export function useFeatureFlag(flag: FeatureFlag): { isEnabled: ComputedRef<boolean> } {
  const isEnabled = computed(() => FEATURE_FLAGS[flag] === true)
  return { isEnabled }
}

/**
 * Feature flag definitions.
 * Values are read from environment variables at build time.
 * Toggle entire modules without modifying business code.
 */

export const FEATURE_FLAGS = {
  NOTIFICATIONS: import.meta.env.VITE_FEATURE_NOTIFICATIONS === 'true',
  REPORTS: import.meta.env.VITE_FEATURE_REPORTS === 'true',
  DOCUMENTS: import.meta.env.VITE_FEATURE_DOCUMENTS === 'true',
  ANALYTICS: import.meta.env.VITE_FEATURE_ANALYTICS === 'true'
} as const

export type FeatureFlag = keyof typeof FEATURE_FLAGS

/**
 * Check if a feature flag is enabled.
 */
export function isFeatureEnabled(flag: FeatureFlag): boolean {
  return FEATURE_FLAGS[flag] === true
}

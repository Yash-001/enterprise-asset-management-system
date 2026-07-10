import mitt from 'mitt'

// ─── Event Constants ──────────────────────────────────────────────────────────

export const EVENT_NAMES = {
  SESSION_EXPIRED: 'SESSION_EXPIRED',
  THEME_CHANGED: 'THEME_CHANGED',
  SIDEBAR_TOGGLE: 'SIDEBAR_TOGGLE',
  NOTIFICATION_RECEIVED: 'NOTIFICATION_RECEIVED',
  TOAST_SHOW: 'TOAST_SHOW',
  GLOBAL_ERROR: 'GLOBAL_ERROR'
} as const

// ─── Event Payload Types ──────────────────────────────────────────────────────

export type EventBusEvents = {
  [EVENT_NAMES.SESSION_EXPIRED]: void
  [EVENT_NAMES.THEME_CHANGED]: { isDark: boolean }
  [EVENT_NAMES.SIDEBAR_TOGGLE]: { collapsed: boolean }
  [EVENT_NAMES.NOTIFICATION_RECEIVED]: { count: number }
  [EVENT_NAMES.TOAST_SHOW]: {
    severity: 'success' | 'info' | 'warn' | 'error'
    summary: string
    detail?: string
  }
  [EVENT_NAMES.GLOBAL_ERROR]: { message: string; code?: number }
}

// ─── Event Bus Instance ───────────────────────────────────────────────────────

/**
 * Application-wide typed event bus.
 * Use for cross-cutting concerns where Pinia stores would create unnecessary coupling.
 *
 * Usage:
 *   import { eventBus, EVENT_NAMES } from '@/shared/services'
 *
 *   // Emit
 *   eventBus.emit(EVENT_NAMES.THEME_CHANGED, { isDark: true })
 *
 *   // Listen
 *   eventBus.on(EVENT_NAMES.THEME_CHANGED, (payload) => { ... })
 *
 *   // Cleanup (in onUnmounted)
 *   eventBus.off(EVENT_NAMES.THEME_CHANGED, handler)
 */
export const eventBus = mitt<EventBusEvents>()

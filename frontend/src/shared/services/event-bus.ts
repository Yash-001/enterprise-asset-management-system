import mitt from 'mitt'

export type EventBusEvents = {
  THEME_CHANGED: { isDark: boolean }
  SIDEBAR_TOGGLE: void
  SESSION_EXPIRED: void
  NOTIFICATION_RECEIVED: void
}

export const eventBus = mitt<EventBusEvents>()

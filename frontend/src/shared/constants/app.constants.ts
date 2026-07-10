export const APP_CONSTANTS = {
  APP_NAME: 'EAMS',
  APP_TITLE: import.meta.env.VITE_APP_TITLE || 'Enterprise Asset Management System',
  DEFAULT_PAGE_SIZE: 20,
  DEBOUNCE_DELAY: 300,
  NOTIFICATION_POLL_INTERVAL: 60000,
  MAX_FILE_SIZE: 50 * 1024 * 1024 // 50MB
} as const

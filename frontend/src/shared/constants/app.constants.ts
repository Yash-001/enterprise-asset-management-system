export const APP_CONSTANTS = {
  APP_NAME: 'EAMS',
  APP_TITLE: 'Enterprise Asset Management System',
  VERSION: '1.0.0',

  // Pagination
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100] as readonly number[],

  // Debounce & Timing
  DEBOUNCE_DELAY: 300,
  NOTIFICATION_POLL_INTERVAL: 60_000,
  TOKEN_REFRESH_THRESHOLD: 5 * 60 * 1000, // 5 minutes before expiry

  // File Upload
  MAX_FILE_SIZE: 50 * 1024 * 1024, // 50MB
  ALLOWED_FILE_EXTENSIONS: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.png', '.jpg', '.jpeg'],

  // Toast
  TOAST_LIFE: 3000,
  TOAST_LIFE_ERROR: 5000,

  // Table
  TABLE_ROW_HOVER: true,
  TABLE_STRIPED: true,
  TABLE_RESPONSIVE_LAYOUT: 'scroll'
} as const

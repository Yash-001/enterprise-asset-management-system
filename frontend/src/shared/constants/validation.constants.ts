/**
 * Validation patterns matching backend @Pattern annotations.
 */
export const VALIDATION_PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  ASSET_CODE: /^[A-Z0-9\-]+$/,
  PHONE: /^\+?[\d\s\-()]{7,20}$/,
  ALPHANUMERIC: /^[A-Za-z0-9]+$/,
  NO_LEADING_TRAILING_SPACES: /^\S(.*\S)?$/,
  PO_NUMBER: /^[A-Z0-9\-]+$/,
  WORK_ORDER_NUMBER: /^[A-Z0-9\-]+$/
} as const

/**
 * Field length limits matching backend @Size annotations.
 */
export const VALIDATION_LIMITS = {
  // Names
  FIRST_NAME_MIN: 1,
  FIRST_NAME_MAX: 100,
  LAST_NAME_MIN: 1,
  LAST_NAME_MAX: 100,

  // Auth
  EMAIL_MAX: 255,
  PASSWORD_MIN: 8,
  PASSWORD_MAX: 100,

  // Asset
  ASSET_CODE_MAX: 100,
  ASSET_NAME_MAX: 150,
  SERIAL_NUMBER_MAX: 100,
  MANUFACTURER_MAX: 100,
  MODEL_MAX: 100,

  // General
  TITLE_MAX: 150,
  DESCRIPTION_MAX: 500,
  REMARKS_MAX: 500,

  // Work Order
  WORK_ORDER_NUMBER_MAX: 100,
  TECHNICIAN_NAME_MAX: 150,

  // Purchase Order
  PO_NUMBER_MAX: 100,

  // Spare Part
  PART_NUMBER_MAX: 100,
  PART_NAME_MAX: 150,
  CATEGORY_MAX: 100,

  // Vendor
  VENDOR_CODE_MAX: 100,
  VENDOR_NAME_MAX: 150,
  CONTACT_PERSON_MAX: 100,

  // Notification
  NOTIFICATION_TITLE_MAX: 150,
  NOTIFICATION_MESSAGE_MAX: 1000,

  // Maintenance
  PLAN_CODE_MAX: 100,
  PLAN_NAME_MAX: 150
} as const

/**
 * Error messages for validation rules.
 */
export const VALIDATION_MESSAGES = {
  REQUIRED: (field: string) => `${field} is required`,
  MIN_LENGTH: (field: string, min: number) => `${field} must be at least ${min} characters`,
  MAX_LENGTH: (field: string, max: number) => `${field} must not exceed ${max} characters`,
  INVALID_EMAIL: 'Please enter a valid email address',
  INVALID_PATTERN: (field: string) => `${field} contains invalid characters`,
  NO_SPACES: (field: string) => `${field} must not contain leading or trailing spaces`,
  PASSWORDS_MISMATCH: 'Passwords do not match',
  POSITIVE_NUMBER: (field: string) => `${field} must be a positive number`,
  FUTURE_DATE: (field: string) => `${field} must be a future date`,
  FILE_TOO_LARGE: (maxMB: number) => `File size must not exceed ${maxMB}MB`
} as const

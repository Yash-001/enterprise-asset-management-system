/**
 * Field-level validation error state.
 */
export interface FieldError {
  field: string
  message: string
}

/**
 * Form validation state tracker.
 */
export interface FormValidationState {
  isValid: boolean
  isDirty: boolean
  isSubmitting: boolean
  errors: Record<string, string>
}

/**
 * Validation rule function signature.
 */
export type ValidationRule = (value: unknown) => string | true

/**
 * Common validation patterns (matching backend constraints).
 */
export const VALIDATION_PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  ASSET_CODE: /^[A-Z0-9\-]+$/,
  PHONE: /^\+?[\d\s\-()]+$/,
  NO_LEADING_TRAILING_SPACES: /^\S.*\S$|^\S$/
} as const

/**
 * Common validation limits (matching backend @Size constraints).
 */
export const VALIDATION_LIMITS = {
  NAME_MIN: 1,
  NAME_MAX: 100,
  EMAIL_MAX: 255,
  PASSWORD_MIN: 8,
  PASSWORD_MAX: 100,
  DESCRIPTION_MAX: 500,
  ASSET_CODE_MAX: 100,
  REMARKS_MAX: 500,
  TITLE_MAX: 150
} as const

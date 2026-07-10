import { VALIDATION_PATTERNS, VALIDATION_MESSAGES } from '@/shared/constants'

/**
 * Reusable validation rule builders.
 * Each returns a function: (value) => errorMessage | true
 */

export function required(fieldName: string) {
  return (value: unknown): string | true => {
    if (value === null || value === undefined || value === '') {
      return VALIDATION_MESSAGES.REQUIRED(fieldName)
    }
    if (typeof value === 'string' && value.trim().length === 0) {
      return VALIDATION_MESSAGES.REQUIRED(fieldName)
    }
    return true
  }
}

export function minLength(fieldName: string, min: number) {
  return (value: unknown): string | true => {
    if (typeof value !== 'string') return true
    if (value.length < min) {
      return VALIDATION_MESSAGES.MIN_LENGTH(fieldName, min)
    }
    return true
  }
}

export function maxLength(fieldName: string, max: number) {
  return (value: unknown): string | true => {
    if (typeof value !== 'string') return true
    if (value.length > max) {
      return VALIDATION_MESSAGES.MAX_LENGTH(fieldName, max)
    }
    return true
  }
}

export function email() {
  return (value: unknown): string | true => {
    if (typeof value !== 'string' || value.length === 0) return true
    if (!VALIDATION_PATTERNS.EMAIL.test(value)) {
      return VALIDATION_MESSAGES.INVALID_EMAIL
    }
    return true
  }
}

export function pattern(fieldName: string, regex: RegExp) {
  return (value: unknown): string | true => {
    if (typeof value !== 'string' || value.length === 0) return true
    if (!regex.test(value)) {
      return VALIDATION_MESSAGES.INVALID_PATTERN(fieldName)
    }
    return true
  }
}

export function noLeadingTrailingSpaces(fieldName: string) {
  return (value: unknown): string | true => {
    if (typeof value !== 'string' || value.length === 0) return true
    if (!VALIDATION_PATTERNS.NO_LEADING_TRAILING_SPACES.test(value)) {
      return VALIDATION_MESSAGES.NO_SPACES(fieldName)
    }
    return true
  }
}

export function positiveNumber(fieldName: string) {
  return (value: unknown): string | true => {
    if (value === null || value === undefined || value === '') return true
    const num = Number(value)
    if (isNaN(num) || num <= 0) {
      return VALIDATION_MESSAGES.POSITIVE_NUMBER(fieldName)
    }
    return true
  }
}

/**
 * Run multiple validation rules against a value.
 * Returns first error or null.
 */
export function validate(value: unknown, rules: Array<(v: unknown) => string | true>): string | null {
  for (const rule of rules) {
    const result = rule(value)
    if (result !== true) return result
  }
  return null
}

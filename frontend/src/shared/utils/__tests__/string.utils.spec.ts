import { describe, it, expect } from 'vitest'
import { truncate, capitalize, toTitleCase, getInitials, pluralize, isBlank } from '../string.utils'

describe('string.utils', () => {
  describe('truncate', () => {
    it('returns original string when shorter than max', () => {
      expect(truncate('hello', 10)).toBe('hello')
    })

    it('truncates and adds ellipsis', () => {
      expect(truncate('hello world', 5)).toBe('hello...')
    })

    it('handles null/undefined', () => {
      expect(truncate(null, 10)).toBe('')
      expect(truncate(undefined, 10)).toBe('')
    })
  })

  describe('capitalize', () => {
    it('capitalizes first letter', () => {
      expect(capitalize('hello')).toBe('Hello')
    })

    it('handles empty string', () => {
      expect(capitalize('')).toBe('')
    })
  })

  describe('toTitleCase', () => {
    it('converts underscores to title case', () => {
      expect(toTitleCase('work_order')).toBe('Work Order')
    })

    it('converts dashes to title case', () => {
      expect(toTitleCase('purchase-order')).toBe('Purchase Order')
    })
  })

  describe('getInitials', () => {
    it('returns initials from names', () => {
      expect(getInitials('John', 'Doe')).toBe('JD')
    })

    it('handles empty strings', () => {
      expect(getInitials('', '')).toBe('')
    })
  })

  describe('pluralize', () => {
    it('returns singular for count 1', () => {
      expect(pluralize(1, 'item')).toBe('1 item')
    })

    it('returns plural for count > 1', () => {
      expect(pluralize(5, 'item')).toBe('5 items')
    })

    it('uses custom plural', () => {
      expect(pluralize(2, 'child', 'children')).toBe('2 children')
    })
  })

  describe('isBlank', () => {
    it('returns true for empty/whitespace', () => {
      expect(isBlank('')).toBe(true)
      expect(isBlank('   ')).toBe(true)
      expect(isBlank(null)).toBe(true)
      expect(isBlank(undefined)).toBe(true)
    })

    it('returns false for non-blank', () => {
      expect(isBlank('hello')).toBe(false)
    })
  })
})

/**
 * String manipulation utilities.
 */

/**
 * Truncate string with ellipsis (e.g., "Enterprise Asset Ma...").
 */
export function truncate(text: string | null | undefined, maxLength: number): string {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength).trimEnd() + '...'
}

/**
 * Capitalize first letter (e.g., "hello" → "Hello").
 */
export function capitalize(text: string | null | undefined): string {
  if (!text) return ''
  return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase()
}

/**
 * Convert to title case (e.g., "work_order" → "Work Order").
 */
export function toTitleCase(text: string | null | undefined): string {
  if (!text) return ''
  return text
    .replace(/[_-]/g, ' ')
    .replace(/\b\w/g, (char) => char.toUpperCase())
}

/**
 * Convert to kebab-case (e.g., "WorkOrder" → "work-order").
 */
export function toKebabCase(text: string): string {
  return text
    .replace(/([a-z])([A-Z])/g, '$1-$2')
    .replace(/[\s_]+/g, '-')
    .toLowerCase()
}

/**
 * Get initials from full name (e.g., "John Doe" → "JD").
 */
export function getInitials(firstName: string, lastName: string): string {
  const f = firstName?.charAt(0)?.toUpperCase() || ''
  const l = lastName?.charAt(0)?.toUpperCase() || ''
  return f + l
}

/**
 * Pluralize a word based on count (e.g., "1 item", "5 items").
 */
export function pluralize(count: number, singular: string, plural?: string): string {
  const word = count === 1 ? singular : (plural || singular + 's')
  return `${count} ${word}`
}

/**
 * Check if string is empty or whitespace only.
 */
export function isBlank(value: string | null | undefined): boolean {
  return !value || value.trim().length === 0
}

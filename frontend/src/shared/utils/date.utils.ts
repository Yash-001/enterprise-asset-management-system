/**
 * Date utility functions for formatting backend ISO date strings.
 */

/**
 * Format ISO date string to locale display (e.g., "Jul 11, 2026").
 */
export function formatDate(isoDate: string | null | undefined): string {
  if (!isoDate) return '—'
  const date = new Date(isoDate)
  if (isNaN(date.getTime())) return '—'
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

/**
 * Format ISO datetime string to locale display (e.g., "Jul 11, 2026, 2:30 PM").
 */
export function formatDateTime(isoDate: string | null | undefined): string {
  if (!isoDate) return '—'
  const date = new Date(isoDate)
  if (isNaN(date.getTime())) return '—'
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    hour12: true
  })
}

/**
 * Format ISO date to YYYY-MM-DD for API requests and form inputs.
 */
export function toISODate(date: Date | string | null | undefined): string {
  if (!date) return ''
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return ''
  return d.toISOString().split('T')[0]
}

/**
 * Get relative time string (e.g., "5 minutes ago", "2 hours ago").
 */
export function timeAgo(isoDate: string | null | undefined): string {
  if (!isoDate) return '—'
  const date = new Date(isoDate)
  if (isNaN(date.getTime())) return '—'

  const seconds = Math.floor((Date.now() - date.getTime()) / 1000)

  if (seconds < 60) return 'Just now'
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`
  if (seconds < 604800) return `${Math.floor(seconds / 86400)}d ago`

  return formatDate(isoDate)
}

/**
 * Check if a date is in the past.
 */
export function isPast(isoDate: string | null | undefined): boolean {
  if (!isoDate) return false
  return new Date(isoDate).getTime() < Date.now()
}

/**
 * Check if a date is today.
 */
export function isToday(isoDate: string | null | undefined): boolean {
  if (!isoDate) return false
  const date = new Date(isoDate)
  const today = new Date()
  return (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  )
}

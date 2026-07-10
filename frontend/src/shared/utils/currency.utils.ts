/**
 * Currency and number formatting utilities.
 */

/**
 * Format number as USD currency (e.g., "$1,234.56").
 */
export function formatCurrency(
  value: number | null | undefined,
  currency = 'USD',
  locale = 'en-US'
): string {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value)
}

/**
 * Format number with locale separators (e.g., "1,234").
 */
export function formatNumber(value: number | null | undefined, locale = 'en-US'): string {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat(locale).format(value)
}

/**
 * Format number as compact (e.g., "1.2K", "3.5M").
 */
export function formatCompact(value: number | null | undefined, locale = 'en-US'): string {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat(locale, {
    notation: 'compact',
    maximumFractionDigits: 1
  }).format(value)
}

/**
 * Format percentage (e.g., "85.5%").
 */
export function formatPercentage(value: number | null | undefined, decimals = 1): string {
  if (value === null || value === undefined) return '—'
  return `${value.toFixed(decimals)}%`
}

/**
 * Structured logger — automatically suppressed in production.
 * Only error() emits in production builds.
 */

const isDev = import.meta.env.DEV

function timestamp(): string {
  return new Date().toISOString()
}

export const logger = {
  info(message: string, ...args: unknown[]): void {
    if (isDev) {
      console.info(`[${timestamp()}] [INFO] ${message}`, ...args) // eslint-disable-line no-console
    }
  },

  warn(message: string, ...args: unknown[]): void {
    if (isDev) {
      console.warn(`[${timestamp()}] [WARN] ${message}`, ...args)
    }
  },

  error(message: string, ...args: unknown[]): void {
    console.error(`[${timestamp()}] [ERROR] ${message}`, ...args)
  },

  debug(message: string, ...args: unknown[]): void {
    if (isDev) {
      console.info(`[${timestamp()}] [DEBUG] ${message}`, ...args) // eslint-disable-line no-console
    }
  },

  /**
   * Log a grouped set of related messages (dev only).
   */
  group(label: string, fn: () => void): void {
    if (isDev) {
      console.group(`[EAMS] ${label}`) // eslint-disable-line no-console
      fn()
      console.groupEnd() // eslint-disable-line no-console
    }
  }
}

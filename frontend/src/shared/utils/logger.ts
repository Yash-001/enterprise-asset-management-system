const isDev = import.meta.env.DEV

export const logger = {
  info(message: string, ...args: unknown[]): void {
    if (isDev) {
      console.info(`[EAMS INFO] ${message}`, ...args) // eslint-disable-line no-console
    }
  },

  warn(message: string, ...args: unknown[]): void {
    if (isDev) {
      console.warn(`[EAMS WARN] ${message}`, ...args)
    }
  },

  error(message: string, ...args: unknown[]): void {
    console.error(`[EAMS ERROR] ${message}`, ...args)
  }
}

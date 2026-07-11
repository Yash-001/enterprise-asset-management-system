import { useToast as usePrimeToast } from 'primevue/usetoast'
import { APP_CONSTANTS } from '@/shared/constants'
import type { ToastSeverity } from '@/shared/types'

/**
 * Composable wrapping PrimeVue toast with enterprise defaults.
 *
 * Usage:
 *   const { showSuccess, showError, showWarn, showInfo } = useAppToast()
 *   showSuccess('Asset created successfully')
 */
export function useAppToast() {
  const toast = usePrimeToast()

  function show(severity: ToastSeverity, summary: string, detail?: string, life?: number): void {
    toast.add({
      severity,
      summary,
      detail,
      life: life ?? (severity === 'error' ? APP_CONSTANTS.TOAST_LIFE_ERROR : APP_CONSTANTS.TOAST_LIFE)
    })
  }

  function showSuccess(summary: string, detail?: string): void {
    show('success', summary, detail)
  }

  function showError(summary: string, detail?: string): void {
    show('error', summary, detail)
  }

  function showWarn(summary: string, detail?: string): void {
    show('warn', summary, detail)
  }

  function showInfo(summary: string, detail?: string): void {
    show('info', summary, detail)
  }

  /**
   * Show toast from API error response.
   */
  function showApiError(error: unknown, fallback = 'An unexpected error occurred'): void {
    const axiosErr = error as { response?: { data?: { message?: string } } }
    const message = axiosErr?.response?.data?.message || fallback
    showError('Error', message)
  }

  return {
    show,
    showSuccess,
    showError,
    showWarn,
    showInfo,
    showApiError
  }
}

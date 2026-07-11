import { useConfirm as usePrimeConfirm } from 'primevue/useconfirm'

/**
 * Composable wrapping PrimeVue ConfirmationService with enterprise defaults.
 *
 * Usage:
 *   const { confirmDelete, confirmAction } = useAppConfirm()
 *   confirmDelete('Asset', () => assetService.delete(id))
 */
export function useAppConfirm() {
  const confirm = usePrimeConfirm()

  function confirmAction(options: {
    header?: string
    message: string
    icon?: string
    acceptLabel?: string
    rejectLabel?: string
    acceptClass?: string
    onAccept: () => void
    onReject?: () => void
  }): void {
    confirm.require({
      header: options.header || 'Confirm',
      message: options.message,
      icon: options.icon || 'pi pi-exclamation-triangle',
      acceptLabel: options.acceptLabel || 'Confirm',
      rejectLabel: options.rejectLabel || 'Cancel',
      acceptClass: options.acceptClass || '',
      accept: options.onAccept,
      reject: options.onReject
    })
  }

  function confirmDelete(entityName: string, onAccept: () => void): void {
    confirmAction({
      header: `Delete ${entityName}`,
      message: `Are you sure you want to delete this ${entityName.toLowerCase()}? This action cannot be undone.`,
      icon: 'pi pi-trash',
      acceptLabel: 'Delete',
      acceptClass: 'p-button-danger',
      onAccept
    })
  }

  return {
    confirmAction,
    confirmDelete
  }
}

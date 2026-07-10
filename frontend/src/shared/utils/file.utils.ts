import { APP_CONSTANTS } from '@/shared/constants'

/**
 * File utility functions for uploads, downloads, and display.
 */

/**
 * Format file size to human-readable string (e.g., "2.5 MB").
 */
export function formatFileSize(bytes: number | null | undefined): string {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const index = Math.floor(Math.log(bytes) / Math.log(1024))
  const size = bytes / Math.pow(1024, index)
  return `${size.toFixed(index === 0 ? 0 : 1)} ${units[index]}`
}

/**
 * Extract file extension from filename (e.g., "report.pdf" → ".pdf").
 */
export function getFileExtension(filename: string): string {
  if (!filename) return ''
  const lastDot = filename.lastIndexOf('.')
  return lastDot === -1 ? '' : filename.slice(lastDot).toLowerCase()
}

/**
 * Extract filename without extension (e.g., "report.pdf" → "report").
 */
export function getFileNameWithoutExtension(filename: string): string {
  if (!filename) return ''
  const lastDot = filename.lastIndexOf('.')
  return lastDot === -1 ? filename : filename.slice(0, lastDot)
}

/**
 * Check if file size is within allowed limit.
 */
export function isFileSizeValid(size: number): boolean {
  return size > 0 && size <= APP_CONSTANTS.MAX_FILE_SIZE
}

/**
 * Check if file extension is allowed.
 */
export function isFileExtensionValid(filename: string): boolean {
  const ext = getFileExtension(filename)
  return APP_CONSTANTS.ALLOWED_FILE_EXTENSIONS.includes(ext)
}

/**
 * Get appropriate icon class for file type.
 */
export function getFileIcon(contentType: string | null | undefined): string {
  if (!contentType) return 'pi pi-file'
  if (contentType.includes('pdf')) return 'pi pi-file-pdf'
  if (contentType.includes('word') || contentType.includes('document')) return 'pi pi-file-word'
  if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'pi pi-file-excel'
  if (contentType.includes('image')) return 'pi pi-image'
  return 'pi pi-file'
}

/**
 * Trigger browser file download from Blob.
 */
export function downloadBlob(blob: Blob, filename: string): void {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

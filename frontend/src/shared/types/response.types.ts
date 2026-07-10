/**
 * Generic service method result — wraps either success data or error.
 * Used by store actions to communicate outcomes to components.
 */
export interface ServiceResult<T> {
  success: boolean
  data: T | null
  error: string | null
}

/**
 * Toast notification severity levels (matches PrimeVue).
 */
export type ToastSeverity = 'success' | 'info' | 'warn' | 'error'

/**
 * Toast message payload.
 */
export interface ToastMessage {
  severity: ToastSeverity
  summary: string
  detail?: string
  life?: number
}

/**
 * Confirm dialog payload.
 */
export interface ConfirmPayload {
  header: string
  message: string
  icon?: string
  acceptLabel?: string
  rejectLabel?: string
  acceptClass?: string
}

/**
 * Notification type enum (matches backend NotificationType).
 */
export type NotificationType = 'SYSTEM' | 'WORK_ORDER' | 'MAINTENANCE' | 'INVENTORY' | 'PURCHASE_ORDER'

/**
 * Notification priority enum (matches backend NotificationPriority).
 */
export type NotificationPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

/**
 * Notification response (matches backend NotificationResponse DTO).
 */
export interface NotificationResponse {
  id: number
  title: string
  message: string
  notificationType: NotificationType
  priority: NotificationPriority
  recipientUserId: number
  read: boolean
  readAt: string | null
  referenceType: string | null
  referenceId: number | null
  createdAt: string
}

/**
 * Audit log action enum (matches backend AuditAction).
 */
export type AuditAction = 'CREATE' | 'UPDATE' | 'DELETE' | 'READ'

/**
 * Audit log response (matches backend AuditLogResponse DTO).
 */
export interface AuditLogResponse {
  id: number
  entityName: string
  action: AuditAction
  beforeValue: string | null
  afterValue: string | null
  performedBy: string
  performedAt: string
  ipAddress: string | null
  entityId: number | null
}

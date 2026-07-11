import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { AuditLogResponse } from '../types'

export class AuditLogService {
  async search(params: Record<string, unknown> = {}): Promise<PageResponse<AuditLogResponse>> {
    return apiClient.getPaged<AuditLogResponse>(ENDPOINTS.AUDIT_LOGS.BASE, params)
  }

  async getEntityHistory(entityName: string, entityId: number): Promise<AuditLogResponse[]> {
    return apiClient.get<AuditLogResponse[]>(ENDPOINTS.AUDIT_LOGS.ENTITY_HISTORY(entityName, entityId))
  }
}

export const auditLogService = new AuditLogService()

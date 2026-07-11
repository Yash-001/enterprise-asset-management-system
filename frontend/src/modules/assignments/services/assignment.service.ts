import { apiClient, ENDPOINTS } from '@/api'
import type { PageResponse } from '@/shared/types'
import type { AssignmentListItem, AssignmentCreatePayload } from '../types'

export class AssignmentService {
  async getHistory(params: Record<string, unknown> = {}): Promise<PageResponse<AssignmentListItem>> {
    return apiClient.getPaged<AssignmentListItem>(
      ENDPOINTS.ASSIGNMENTS.BASE + '/history',
      params
    )
  }

  async getById(id: number): Promise<AssignmentListItem> {
    return apiClient.get<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.BY_ID(id))
  }

  async create(payload: AssignmentCreatePayload): Promise<AssignmentListItem> {
    return apiClient.post<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.BASE, payload)
  }

  async returnAsset(id: number, returnedDate?: string, remarks?: string): Promise<AssignmentListItem> {
    const params: Record<string, string> = {}
    if (returnedDate) params.returnedDate = returnedDate
    if (remarks) params.remarks = remarks
    return apiClient.post<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.RETURN(id), null, { params })
  }
}

export const assignmentService = new AssignmentService()

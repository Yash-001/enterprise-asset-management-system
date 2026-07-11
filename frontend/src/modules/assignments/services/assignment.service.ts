import { apiClient, ENDPOINTS } from '@/api'
import type { AssignmentListItem, AssignmentCreatePayload } from '../types'

export class AssignmentService {
  async getAll(): Promise<AssignmentListItem[]> {
    return apiClient.get<AssignmentListItem[]>(ENDPOINTS.ASSIGNMENTS.BASE)
  }

  async getById(id: number): Promise<AssignmentListItem> {
    return apiClient.get<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.BY_ID(id))
  }

  async create(payload: AssignmentCreatePayload): Promise<AssignmentListItem> {
    return apiClient.post<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.BASE, payload)
  }

  async returnAsset(id: number): Promise<AssignmentListItem> {
    return apiClient.put<AssignmentListItem>(ENDPOINTS.ASSIGNMENTS.RETURN(id), {})
  }
}

export const assignmentService = new AssignmentService()

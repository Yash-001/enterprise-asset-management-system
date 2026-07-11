import { apiClient, ENDPOINTS } from '@/api'
import type { DepartmentListItem, DepartmentCreatePayload } from '../types'

export class DepartmentService {
  async getAll(): Promise<DepartmentListItem[]> {
    return apiClient.get<DepartmentListItem[]>(ENDPOINTS.DEPARTMENTS.BASE)
  }

  async getById(id: number): Promise<DepartmentListItem> {
    return apiClient.get<DepartmentListItem>(ENDPOINTS.DEPARTMENTS.BY_ID(id))
  }

  async create(payload: DepartmentCreatePayload): Promise<DepartmentListItem> {
    return apiClient.post<DepartmentListItem>(ENDPOINTS.DEPARTMENTS.BASE, payload)
  }

  async update(id: number, payload: Partial<DepartmentCreatePayload>): Promise<DepartmentListItem> {
    return apiClient.put<DepartmentListItem>(ENDPOINTS.DEPARTMENTS.BY_ID(id), payload)
  }

  async delete(id: number): Promise<void> {
    return apiClient.delete(ENDPOINTS.DEPARTMENTS.BY_ID(id))
  }
}

export const departmentService = new DepartmentService()

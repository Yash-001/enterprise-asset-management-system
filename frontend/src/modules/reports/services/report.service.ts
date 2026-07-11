import { apiClient, ENDPOINTS } from '@/api'
import type { ReportFilter } from '../types'

export class ReportService {
  async generate(filters: ReportFilter): Promise<unknown> {
    return apiClient.post(ENDPOINTS.REPORTS.BASE, filters)
  }
}

export const reportService = new ReportService()

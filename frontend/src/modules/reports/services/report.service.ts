import { apiClient, ENDPOINTS } from '@/api'

export class ReportService {
  async getDashboard(): Promise<unknown> {
    return apiClient.get(ENDPOINTS.REPORTS.DASHBOARD)
  }
}

export const reportService = new ReportService()

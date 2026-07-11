import { apiClient, ENDPOINTS } from '@/api'
import type { DashboardStats } from '../types'

/**
 * Dashboard API service.
 * Aggregates data from multiple backend endpoints for the dashboard view.
 */
export class DashboardService {
  async getStats(): Promise<DashboardStats> {
    // Fetch counts from multiple endpoints in parallel
    const [assets, workOrders, maintenance, inventory] = await Promise.allSettled([
      apiClient.get<{ totalElements: number }>(ENDPOINTS.ASSETS.BASE + '?size=1'),
      apiClient.get<{ totalElements: number }>(ENDPOINTS.WORK_ORDERS.BASE + '?size=1'),
      apiClient.get<{ content: unknown[] }>(ENDPOINTS.MAINTENANCE.DASHBOARD),
      apiClient.get<{ content: unknown[] }>(ENDPOINTS.SPARE_PARTS.LOW_STOCK + '?size=1')
    ])

    return {
      totalAssets: this.extractTotal(assets),
      openWorkOrders: this.extractTotal(workOrders),
      overdueMaintenance: this.extractOverdue(maintenance),
      lowStockItems: this.extractTotal(inventory),
      pendingPurchaseOrders: 0,
      activeAssignments: 0
    }
  }

  private extractTotal(result: PromiseSettledResult<{ totalElements?: number }>): number {
    if (result.status === 'fulfilled' && result.value?.totalElements !== undefined) {
      return result.value.totalElements
    }
    return 0
  }

  private extractOverdue(result: PromiseSettledResult<{ overdueMaintenance?: unknown[] }>): number {
    if (result.status === 'fulfilled' && Array.isArray((result.value as { overdueMaintenance?: unknown[] })?.overdueMaintenance)) {
      return (result.value as { overdueMaintenance: unknown[] }).overdueMaintenance.length
    }
    return 0
  }
}

export const dashboardService = new DashboardService()

export type MaintenanceStatus = 'SCHEDULED' | 'IN_PROGRESS' | 'OVERDUE' | 'COMPLETED' | 'CANCELLED'

export interface MaintenancePlanListItem {
  id: number
  assetId: number
  planCode: string
  planName: string
  description: string | null
  maintenanceType: string
  frequencyType: string
  frequencyValue: number
  nextMaintenanceDate: string | null
  lastMaintenanceDate: string | null
  priority: string
  estimatedDurationHours?: number | null
  status: MaintenanceStatus
  active: boolean
  createdAt: string
}

export interface MaintenancePlanCreatePayload {
  assetId: number
  planCode: string
  planName: string
  description?: string
  maintenanceType: string
  frequencyType: string
  frequencyValue: number
  nextMaintenanceDate?: string
  priority: string
  status?: MaintenanceStatus
}

export interface MaintenanceSearchFilters {
  planCode?: string
  planName?: string
  maintenanceType?: string
  status?: MaintenanceStatus | ''
  priority?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

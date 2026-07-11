export type WorkOrderStatus = 'REQUESTED' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
export type MaintenancePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export interface WorkOrderListItem {
  id: number
  workOrderNumber: string
  assetId: number
  maintenancePlanId: number | null
  title: string
  description: string | null
  assignedTechnician: string | null
  priority: MaintenancePriority
  status: WorkOrderStatus
  scheduledDate: string | null
  startDate: string | null
  completionDate: string | null
  estimatedHours: number | null
  actualHours: number | null
  remarks: string | null
  active: boolean
  createdAt: string
  updatedAt: string
  createdBy: string | null
  updatedBy: string | null
}

export interface WorkOrderCreatePayload {
  workOrderNumber: string
  assetId: number
  maintenancePlanId?: number
  title: string
  description?: string
  assignedTechnician?: string
  priority: MaintenancePriority
  status?: WorkOrderStatus
  scheduledDate?: string
  estimatedHours?: number
  remarks?: string
}

export interface WorkOrderSearchFilters {
  workOrderNumber?: string
  title?: string
  status?: WorkOrderStatus | ''
  priority?: MaintenancePriority | ''
  assignedTechnician?: string
  assetId?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

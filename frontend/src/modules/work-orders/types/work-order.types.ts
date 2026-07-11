export type WorkOrderStatus = 'REQUESTED' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'

export interface WorkOrderListItem {
  id: number
  workOrderNumber: string
  assetId: number
  title: string
  description: string | null
  assignedTechnician: string | null
  priority: string
  status: WorkOrderStatus
  scheduledDate: string | null
  startDate: string | null
  completionDate: string | null
  estimatedHours: number | null
  actualHours: number | null
  remarks: string | null
  active: boolean
  createdAt: string
}

export interface WorkOrderCreatePayload {
  assetId: number
  title: string
  description?: string
  assignedTechnician?: string
  priority: string
  status?: WorkOrderStatus
  scheduledDate?: string
  estimatedHours?: number
  remarks?: string
}

export interface WorkOrderSearchFilters {
  workOrderNumber?: string
  title?: string
  status?: WorkOrderStatus | ''
  priority?: string
  assignedTechnician?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export type AssignmentStatus = 'ACTIVE' | 'RETURNED'

export interface AssignmentListItem {
  id: number
  assetId: number
  employeeId: number
  status: AssignmentStatus
  assignedDate: string
  returnedDate: string | null
  remarks: string | null
  createdAt: string
}

export interface AssignmentCreatePayload {
  assetId: number
  employeeId: number
  assignedDate: string
  remarks?: string
}

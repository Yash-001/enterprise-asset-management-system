export type AssignmentStatus = 'ACTIVE' | 'RETURNED'

export interface AssignmentListItem {
  id: number
  assetId: number
  employeeId: number
  status: AssignmentStatus
  assignedDate: string
  expectedReturnDate: string | null
  returnedDate: string | null
  remarks: string | null
}

export interface AssignmentCreatePayload {
  assetId: number
  employeeId: number
  assignedDate: string
  expectedReturnDate: string
  remarks?: string
}

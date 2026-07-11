export interface DepartmentListItem {
  id: number
  departmentCode: string
  departmentName: string
  description: string | null
  managerId: number | null
  active: boolean
  createdAt: string
}

export interface DepartmentCreatePayload {
  departmentCode: string
  departmentName: string
  description?: string
  managerId?: number
  active?: boolean
}

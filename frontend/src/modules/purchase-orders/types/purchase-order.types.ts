export type PurchaseOrderStatus = 'DRAFT' | 'APPROVED' | 'ORDERED' | 'RECEIVED' | 'CANCELLED'

export interface PurchaseOrderListItem {
  id: number
  poNumber: string
  vendorId: number
  totalAmount: number
  status: PurchaseOrderStatus
  expectedDeliveryDate: string | null
  remarks: string | null
  active: boolean
  createdAt: string
}

export interface PurchaseOrderCreatePayload {
  poNumber: string
  vendorId: number
  totalAmount: number
  status?: PurchaseOrderStatus
  expectedDeliveryDate?: string
  remarks?: string
  active?: boolean
}

export interface PurchaseOrderSearchFilters {
  poNumber?: string
  status?: PurchaseOrderStatus | ''
  vendorId?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

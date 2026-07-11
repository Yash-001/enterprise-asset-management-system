export interface SparePartListItem {
  id: number
  partNumber: string
  partName: string
  category: string
  currentStock: number
  minimumStock: number
  unitCost: number
  locationId: number | null
  supplierId: number | null
  active: boolean
  createdAt: string
}

export interface SparePartCreatePayload {
  partNumber: string
  partName: string
  category: string
  currentStock: number
  minimumStock: number
  unitCost: number
  locationId?: number
  supplierId?: number
  active?: boolean
}

export interface SparePartSearchFilters {
  partNumber?: string
  partName?: string
  category?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

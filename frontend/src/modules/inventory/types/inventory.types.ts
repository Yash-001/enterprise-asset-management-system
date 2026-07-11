export interface SparePartListItem {
  id: number
  partNumber: string
  partName: string
  description: string | null
  manufacturer: string | null
  category: string | null
  unitOfMeasure: string | null
  minimumStock: number
  maximumStock: number | null
  currentStock: number
  unitCost: number
  supplierId: number | null
  locationId: number | null
  active: boolean
  createdAt: string
  updatedAt: string
  createdBy: string | null
  updatedBy: string | null
}

export interface SparePartCreatePayload {
  partNumber: string
  partName: string
  description?: string
  manufacturer?: string
  category?: string
  unitOfMeasure?: string
  minimumStock: number
  maximumStock?: number
  currentStock: number
  unitCost: number
  supplierId?: number
  locationId?: number
  active?: boolean
}

export interface SparePartSearchFilters {
  partNumber?: string
  partName?: string
  category?: string
  manufacturer?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

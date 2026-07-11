export type AssetStatus = 'AVAILABLE' | 'ASSIGNED' | 'IN_MAINTENANCE' | 'DISPOSED'

export interface AssetListItem {
  id: number
  assetCode: string
  assetName: string
  description: string | null
  serialNumber: string | null
  manufacturer: string | null
  model: string | null
  purchaseDate: string
  purchasePrice: number
  warrantyExpiry: string | null
  status: AssetStatus
  departmentId: number | null
  locationId: number | null
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface AssetCreatePayload {
  assetCode: string
  assetName: string
  description?: string
  serialNumber?: string
  manufacturer?: string
  model?: string
  purchaseDate: string
  purchasePrice: number
  warrantyExpiry?: string
  status: AssetStatus
  departmentId?: number
  locationId?: number
  active?: boolean
}

export interface AssetUpdatePayload {
  assetName?: string
  description?: string
  serialNumber?: string
  manufacturer?: string
  model?: string
  purchaseDate?: string
  purchasePrice?: number
  warrantyExpiry?: string
  status?: AssetStatus
  departmentId?: number
  locationId?: number
  active?: boolean
}

export interface AssetSearchFilters {
  assetCode?: string
  assetName?: string
  manufacturer?: string
  status?: AssetStatus | ''
  departmentId?: number
  locationId?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

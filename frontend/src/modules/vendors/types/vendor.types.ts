export interface VendorListItem {
  id: number
  vendorCode: string
  vendorName: string
  contactPerson: string | null
  email: string | null
  phone: string | null
  address: string | null
  active: boolean
  createdAt: string
}

export interface VendorCreatePayload {
  vendorCode: string
  vendorName: string
  contactPerson?: string
  email?: string
  phone?: string
  address?: string
  active?: boolean
}

export interface VendorSearchFilters {
  vendorCode?: string
  vendorName?: string
  contactPerson?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

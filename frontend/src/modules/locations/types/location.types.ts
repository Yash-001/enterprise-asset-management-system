export interface LocationListItem {
  id: number
  locationCode: string
  locationName: string
  address: string | null
  city: string | null
  state: string | null
  country: string | null
  active: boolean
  createdAt: string
}

export interface LocationCreatePayload {
  locationCode: string
  locationName: string
  address?: string
  city?: string
  state?: string
  country?: string
  active?: boolean
}

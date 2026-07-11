export interface DashboardStats {
  totalAssets: number
  openWorkOrders: number
  overdueMaintenance: number
  lowStockItems: number
  pendingPurchaseOrders: number
  activeAssignments: number
}

export interface RecentActivity {
  id: number
  type: 'ASSET' | 'WORK_ORDER' | 'MAINTENANCE' | 'PURCHASE_ORDER' | 'NOTIFICATION'
  title: string
  description: string
  timestamp: string
  icon: string
  color: string
}

export interface ChartData {
  labels: string[]
  datasets: ChartDataset[]
}

export interface ChartDataset {
  label?: string
  data: number[]
  backgroundColor: string | string[]
  borderColor?: string
  borderWidth?: number
}

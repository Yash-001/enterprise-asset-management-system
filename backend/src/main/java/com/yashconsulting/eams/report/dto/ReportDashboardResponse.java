package com.yashconsulting.eams.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDashboardResponse {
    private Map<String, Long> assetsByStatus;
    private Map<String, Long> assetsByDepartment;
    private Map<String, Long> assetsByLocation;
    private Long upcomingMaintenanceCount;
    private Long completedWorkOrdersCount;
    private Long openWorkOrdersCount;
    private BigDecimal inventoryValuation;
    private Long lowStockItemsCount;
    private Map<String, Long> purchaseOrdersByStatus;
    private List<VendorPerformanceResponse> vendorPerformance;
}
